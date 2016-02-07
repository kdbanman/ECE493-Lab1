#pragma version(1)
#pragma rs java_package_name(ece493.kdbanman.ImageWarps) )

#define PARABOLA_WARP (0)
#define FISHEYE_WARP   (1)
#define SWIRL_WARP    (2)

rs_allocation inputAllocation;
rs_allocation outputAllocation;
rs_script scriptContext;

int32_t width;
int32_t height;

float warpParameter;


static int warpType;

static float modulusClamp_float(float value, float max) {
    value = value < 0 ? max - value : value;
    value = value >= max ? remainder(value, max) : value;
 
    return value;
}

static float4 getRGBA(rs_allocation allocation, float floatX, float floatY) {
    uint32_t x = (uint32_t)modulusClamp_float(floatX, (float)width);
    uint32_t y = (uint32_t)modulusClamp_float(floatY, (float)height);
    uchar4 pixelBytes = rsGetElementAt_uchar4(inputAllocation, x, y);

    return rsUnpackColor8888(pixelBytes);
}

static float4 elementwiseInterpolate(float4 start,  float4 finish,  float delta) {
    float4 interpolated;

    interpolated.w = start.w * (1.f - delta) + finish.w * delta;
    interpolated.x = start.x * (1.f - delta) + finish.x * delta;
    interpolated.y = start.y * (1.f - delta) + finish.y * delta;
    interpolated.z = start.z * (1.f - delta) + finish.z * delta;

    return interpolated;
}

static float2 parabolaWarp(uint32_t targetX, uint32_t targetY) {
    float2 source;

    source.x = targetX;
    source.y = targetY + (targetX - width / 2) * (targetX - width / 2) * warpParameter * 0.000003;

    return source;
}

static float2 fisheyeWarp(uint32_t targetX, uint32_t targetY) {
    float2 source, centre, target, centreToTarget;

    centre.x = (float)width / 2;
    centre.y = (float)height / 2;

    target.x = (float)targetX;
    target.y = (float)targetY;

    centreToTarget.x = target.x - centre.x;
    centreToTarget.y = target.y - centre.y;

    float centreToTargetLength = sqrt(centreToTarget.x * centreToTarget.x + centreToTarget.y * centreToTarget.y);
    float diagonalLength = sqrt((float)height * height + (float)width * width);

    float sourceFraction = 1.1 - 0.0005 * warpParameter * diagonalLength / 2.f / centreToTargetLength;
    sourceFraction = max(sourceFraction, 0.3);
    sourceFraction = min(sourceFraction, 1.0);

    if (warpParameter < 0)
        sourceFraction = -1 * sourceFraction;

    source.x = centre.x + centreToTarget.x * sourceFraction;
    source.y = centre.y + centreToTarget.y * sourceFraction;

    return source;
}

static float2 swirlWarp(uint32_t targetX, uint32_t targetY) {
    float2 source;

    source.x = targetX;
    source.y = targetY + (targetX - width / 2) * (targetX - width / 2) * warpParameter * 0.000003;

    return source;
}

static uchar4 interpolatedColor(float2 sourceCoord) {
    float2 upperLeft;
    upperLeft.x = floor(sourceCoord.x);
    upperLeft.y = floor(sourceCoord.y);
        
    float2 bottomRight;
    bottomRight.x = floor(sourceCoord.x);
    bottomRight.y = floor(sourceCoord.y);

    float2 delta;
    delta.x = sourceCoord.x - upperLeft.x;
    delta.y = sourceCoord.y - upperLeft.y;

    float4 upperLeftPixel   = getRGBA(inputAllocation, upperLeft.x,   upperLeft.y);
    float4 upperRightPixel  = getRGBA(inputAllocation, bottomRight.x, upperLeft.y);
    float4 bottomLeftPixel  = getRGBA(inputAllocation, upperLeft.x,   bottomRight.y);
    float4 bottomRightPixel = getRGBA(inputAllocation, bottomRight.x, bottomRight.y);

    float4 interpolatedTopPixel    = elementwiseInterpolate(upperLeftPixel,  upperRightPixel,  delta.x);
    float4 interpolatedBottomPixel = elementwiseInterpolate(bottomLeftPixel, bottomRightPixel, delta.x);

    float4 finalInterpolated = elementwiseInterpolate(interpolatedTopPixel, interpolatedBottomPixel, delta.y);

    return rsPackColorTo8888(finalInterpolated);
}

void root(const uchar4 *inputVector, uchar4 *outputVector, const void *userData, uint32_t x, uint32_t y) {
    float2 sourceCoord;

    switch (warpType) {
        case PARABOLA_WARP:
                sourceCoord = parabolaWarp(x, y);
                break;
        case FISHEYE_WARP:
                sourceCoord = fisheyeWarp(x, y);
                break;
        case SWIRL_WARP:
                sourceCoord = swirlWarp(x, y);
                break;

    }
    *outputVector = interpolatedColor(sourceCoord);
}

void parabola_warp() {
    warpType = PARABOLA_WARP;
    rsForEach(scriptContext, inputAllocation, outputAllocation);
}

void fisheye_warp() {
    warpType = FISHEYE_WARP;
    rsForEach(scriptContext, inputAllocation, outputAllocation);
}

void swirl_warp() {
    warpType = SWIRL_WARP;
    rsForEach(scriptContext, inputAllocation, outputAllocation);
}
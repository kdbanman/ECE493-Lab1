#pragma version(1)
#pragma rs java_package_name(ece493.kdbanman.ImageWarps) )

rs_allocation inputAllocation;
rs_allocation outputAllocation;
rs_script scriptContext;

int32_t width;
int32_t height;

static uint32_t clampValue(uint32_t value, uint32_t min, uint32_t max) {
    value = value < min ? min : value;
    value = value > max ? max : value;

    return value;
}

static float2 dumbwarp(uint32_t targetX, uint32_t targetY) {
    float2 source;

    source.x = targetX;
    // TODO this is parabola, adjust 0.001 by scroll amount
    source.y = targetY + (targetX - width / 2) * (targetX - width / 2) * 0.001;

    return source;
}

static uchar4 interpolatedColor(float2 sourceCoord) {
    //TODO get nbrhood pixels, interpolate between them.  need to rsUnpackColorTo888 (to float4) for each nbrhood pixel, interpolate according to distance from each

    uint32_t sourceX = (uint32_t)sourceCoord.x;
    uint32_t sourceY = (uint32_t)sourceCoord.y;

    sourceX = clampValue(sourceX, 0, width - 1);
    sourceY = clampValue(sourceY, 0, height - 1);

    uchar4 sourcePixel = rsGetElementAt_uchar4(inputAllocation, sourceX, sourceY);

    return sourcePixel;
}

void root(const uchar4 *inputVector, uchar4 *outputVector, const void *userData, uint32_t x, uint32_t y) {
    float2 sourceCoord = dumbwarp(x, y);
    *outputVector = interpolatedColor(sourceCoord);
}

void filter() {
    rsForEach(scriptContext, inputAllocation, outputAllocation);
}
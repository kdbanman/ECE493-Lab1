package ece493.kdbanman.ImageWarps;

import android.content.Context;

import ece493.kdbanman.Model.Filterable;

/**
 * Created by kdban on 2/5/2016.
 */
public class FisheyeOnPinchCallback extends RenderscriptGestureCallback {

    public FisheyeOnPinchCallback(Context context, Filterable image) {
        super(image, context);
    }

    @Override
    protected void invokeRenderscriptWarp(ScriptC_imagewarp imagewarpScript, float pinchDelta) {

        if (pinchDelta <= 0) {
            // An inward pinch for the "zooming in" feel of barrel distortion is counterintuitive,
            // so don't allow it.
            throw new IllegalArgumentException("Pinch with negative delta is not fisheyeable.");
        }

        imagewarpScript.invoke_fisheye_warp();
    }
}

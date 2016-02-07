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
    protected void invokeRenderscriptWarp(ScriptC_imagewarp imagewarpScript) {
        imagewarpScript.invoke_fisheye_warp();
    }
}

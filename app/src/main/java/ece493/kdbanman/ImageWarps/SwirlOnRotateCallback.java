package ece493.kdbanman.ImageWarps;

import android.content.Context;
import android.util.Log;

import ece493.kdbanman.Model.Filterable;

/**
 * Created by kdban on 2/5/2016.
 */
public class SwirlOnRotateCallback extends RenderscriptGestureCallback {

    public SwirlOnRotateCallback(Context context, Filterable image) {
        super(image, context);
    }

    @Override
    protected void invokeRenderscriptWarp(ScriptC_imagewarp imagewarpScript, float gestureParam) {
        imagewarpScript.invoke_swirl_warp();
    }
}

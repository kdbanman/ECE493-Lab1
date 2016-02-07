package ece493.kdbanman.ImageWarps;

import android.content.Context;

import ece493.kdbanman.Gesture.GestureCallback;
import ece493.kdbanman.Model.Filterable;

/**
 * Created by kdban on 2/5/2016.
 */
public class ParabolaOnScrollCallback extends RenderscriptGestureCallback {

    public ParabolaOnScrollCallback(Context context, Filterable image) {
        super(image, context);
    }

    @Override
    protected void invokeRenderscriptWarp(ScriptC_imagewarp imagewarpScript, float gestureParam) {
        imagewarpScript.invoke_parabola_warp();
    }
}

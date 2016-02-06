package ece493.kdbanman.ImageWarps;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;
import android.renderscript.Allocation;

import ece493.kdbanman.Gesture.GestureCallback;
import ece493.kdbanman.Model.Filterable;

/**
 * Created by kdban on 2/5/2016.
 */
public class BarrelOnPinchCallback extends RenderscriptGestureCallback {

    public BarrelOnPinchCallback(Context context, Filterable image) {
        super(image, context);
    }

    @Override
    protected void invokeRenderscriptWarp(ScriptC_imagewarp imagewarpScript) {
        imagewarpScript.invoke_barrel_warp();
    }
}

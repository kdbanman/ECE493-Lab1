package ece493.kdbanman.ImageWarps;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;

import ece493.kdbanman.Gesture.GestureCallback;
import ece493.kdbanman.Model.Filterable;

/**
 * Created by kdban on 2/5/2016.
 */
abstract class RenderscriptGestureCallback implements GestureCallback {
    protected RenderScript renderScript;
    protected Filterable image;
    private Allocation inputAllocation;
    private Allocation outputAllocation;
    private ScriptC_imagewarp imagewarpScript;

    public RenderscriptGestureCallback(Filterable image, Context context) {
        this.image = image;
        renderScript = RenderScript.create(context);
    }

    /**
     * @param gestureParam Absolute value may be hundreds.  Positive for expansion pinch.
     */
    @Override
    public void executeGesture(float gestureParam) {
        if (!image.hasImage()) {
            throw new IllegalArgumentException("Set image before executing gesture.");
        }

        Bitmap outputBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

        inputAllocation = Allocation.createFromBitmap(renderScript, image.getImageReference());
        outputAllocation = Allocation.createFromBitmap(renderScript, outputBitmap);

        imagewarpScript = new ScriptC_imagewarp(renderScript);

        imagewarpScript.set_inputAllocation(inputAllocation);
        imagewarpScript.set_outputAllocation(outputAllocation);
        imagewarpScript.set_scriptContext(imagewarpScript);

        imagewarpScript.set_width(image.getWidth());
        imagewarpScript.set_height(image.getHeight());

        imagewarpScript.set_warpParameter(gestureParam);

        try {
            invokeRenderscriptWarp(imagewarpScript, gestureParam);
        } catch (IllegalArgumentException e) {
            Log.i("invokeRenderscriptWarp", e.getMessage());
            return;
        } catch (Exception e) {
            Log.w("invokeRenderscriptWarp", e.getMessage());
            return;
        }

        outputAllocation.copyTo(outputBitmap);

        image.setImage(outputBitmap);
    }

    protected abstract void invokeRenderscriptWarp(ScriptC_imagewarp imagewarpScript, float gestureParam);
}

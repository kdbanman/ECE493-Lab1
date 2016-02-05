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
public class BarrelOnPinchCallback implements GestureCallback {

    private RenderScript renderScript;
    private Allocation inputAllocation, outputAllocation;
    private ScriptC_barrelwarp barrelwarpScript;

    private Filterable image;

    public BarrelOnPinchCallback(Context context, Filterable image) {
        this.image = image;

        renderScript = RenderScript.create(context);
    }

    /**
     * @param pinchDelta Absolute value may be hundreds.  Positive for expansion pinch.
     */
    @Override
    public void executeGesture(float pinchDelta) {
        if (!image.hasImage()) {
            throw new IllegalArgumentException("Set image before executing gesture.");
        }

        Bitmap outputBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());

        inputAllocation = Allocation.createFromBitmap(renderScript, image.getImageReference());
        outputAllocation = Allocation.createFromBitmap(renderScript, outputBitmap);

        barrelwarpScript = new ScriptC_barrelwarp(renderScript);

        barrelwarpScript.set_inputAllocation(inputAllocation);
        barrelwarpScript.set_outputAllocation(outputAllocation);
        barrelwarpScript.set_scriptContext(barrelwarpScript);

        barrelwarpScript.set_width(image.getWidth());
        barrelwarpScript.set_height(image.getHeight());

        barrelwarpScript.invoke_filter();

        outputAllocation.copyTo(outputBitmap);

        image.setImage(outputBitmap);
    }
}

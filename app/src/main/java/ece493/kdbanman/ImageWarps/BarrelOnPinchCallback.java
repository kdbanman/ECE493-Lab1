package ece493.kdbanman.ImageWarps;

import android.renderscript.RenderScript;

import ece493.kdbanman.Gesture.GestureCallback;

/**
 * Created by kdban on 2/5/2016.
 */
public class BarrelOnPinchCallback implements GestureCallback {

    private RenderScript renderScript;

    /**
     * @param pinchDelta Absolute value may be hundreds.  Positive for expansion pinch.
     */
    @Override
    public void executeGesture(float pinchDelta, int[] argbPixels, int width, int height) {

    }
}

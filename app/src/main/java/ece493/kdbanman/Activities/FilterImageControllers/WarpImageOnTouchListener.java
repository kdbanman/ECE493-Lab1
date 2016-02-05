package ece493.kdbanman.Activities.FilterImageControllers;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

import ece493.kdbanman.Gesture.GestureBuilder;
import ece493.kdbanman.Gesture.GestureCallback;
import ece493.kdbanman.Gesture.GestureType;
import ece493.kdbanman.ImageWarps.BarrelOnPinchCallback;
import ece493.kdbanman.MessageCallback;
import ece493.kdbanman.Model.Filterable;

/**
 * A touch listener class that detects touch gestures and applies image warps accordingly.
 *
 * Created by kdban on 2/5/2016.
 */
public class WarpImageOnTouchListener implements View.OnTouchListener {

    GestureBuilder gestureBuilder;

    MessageCallback errorReporter;

    public WarpImageOnTouchListener(HashMap<GestureType, GestureCallback> callbacks, MessageCallback onError, int touchSlop) {
        this.gestureBuilder = new GestureBuilder(touchSlop, callbacks);
        this.errorReporter = onError;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            int action = (event.getAction() & MotionEvent.ACTION_MASK);

            if (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP && event.getPointerCount() == 0) {
                gestureBuilder.executeGesture();
                gestureBuilder.reset();
                return true;
            }

            return gestureBuilder.addEvent(event);
        } catch (IllegalArgumentException e) {
            errorReporter.report(e.getMessage());
            gestureBuilder.reset();
            return true;
        }
    }
}

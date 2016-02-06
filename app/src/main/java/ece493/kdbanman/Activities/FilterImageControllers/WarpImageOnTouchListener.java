package ece493.kdbanman.Activities.FilterImageControllers;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

import ece493.kdbanman.Gesture.Gesture;
import ece493.kdbanman.Gesture.GestureBuilder;
import ece493.kdbanman.Gesture.GestureCallback;
import ece493.kdbanman.Gesture.MotionType;

/**
 * A touch listener class that detects touch gestures and applies image warps accordingly.
 *
 * Created by kdban on 2/5/2016.
 */
public class WarpImageOnTouchListener implements View.OnTouchListener {

    private HashMap<MotionType, GestureCallback> callbacks;

    private int touchSlop;
    private GestureBuilder gestureBuilder;

    public WarpImageOnTouchListener(HashMap<MotionType, GestureCallback> callbacks, int touchSlop) {

        this.callbacks = callbacks;
        this.touchSlop = touchSlop;
        this.gestureBuilder = new GestureBuilder(touchSlop, callbacks);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            int action = (event.getAction() & MotionEvent.ACTION_MASK);

            if (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP && event.getPointerCount() == 0) {
                Gesture gesture = gestureBuilder.buildGesture();
                if (gesture != null) {
                    gesture.execute();
                }

                gestureBuilder = new GestureBuilder(touchSlop, callbacks);

                return true;
            }

            return gestureBuilder.addEvent(event);

        } catch (IllegalArgumentException e) {
            Log.e("WarpImageListener", e.getMessage());
            gestureBuilder = new GestureBuilder(touchSlop, callbacks);
            return true;
        }
    }
}

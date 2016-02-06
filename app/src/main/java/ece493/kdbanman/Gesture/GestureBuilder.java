package ece493.kdbanman.Gesture;

import android.util.Log;
import android.view.MotionEvent;

import java.util.HashMap;

/**
 * Builder pattern frontend to the Gesture class:
 * Construct the builder, add events, build the gesture.
 *
 * Created by kdban on 2/5/2016.
 */
public class GestureBuilder {

    HashMap<MotionType, GestureCallback> callbacks;

    private int touchSlop = 0;

    private float
            firstTouchStartX = 0,
            firstTouchStartY = 0,
            secondTouchStartX = 0,
            secondTouchStartY = 0,
            pinchStartDistance = 0;

    private float finalGestureValue = 0;
    private MotionType finalGestureType = MotionType.PINCH;

    /**
     * @param touchSlop The threshold for movement under which gestures should be ignored.
     * @param callbacks Callbacks for each type of movement.
     */
    public GestureBuilder(int touchSlop, HashMap<MotionType, GestureCallback> callbacks) {
        this.touchSlop = touchSlop;

        this.callbacks = callbacks;
    }

    public boolean addEvent(MotionEvent event) {
        int action = (event.getAction() & MotionEvent.ACTION_MASK);

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                addTouchDownEvent(event);
                break;

            case MotionEvent.ACTION_MOVE:
                boolean firstTouchMoving = motionExceedsSlopThreshold(event, 0, firstTouchStartX, firstTouchStartY);
                boolean secondTouchMoving = motionExceedsSlopThreshold(event, 1, secondTouchStartX, secondTouchStartY);
                if (firstTouchMoving && secondTouchMoving) {
                    addDoubleTouchMoveEvent(event);
                }
                break;
        }

        return true;
    }

    public Gesture buildGesture() {
        Log.d("GestureBuilder", String.format("BUILDING GESTURE %s WITH VALUE %.5f", finalGestureType, finalGestureValue));

        if (callbacks != null && callbacks.containsKey(finalGestureType)) {
            return new Gesture(finalGestureValue, callbacks.get(finalGestureType));
        }

        return null;
    }

    // END PUBLIC METHODS
    ////////////////


    private void addTouchDownEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            firstTouchStartX = event.getX(0);
            firstTouchStartY = event.getY(0);

            Log.d("GestureBuilder", String.format("POINTER ONE DOWN X = %.5f, Y = %.5f", firstTouchStartX, firstTouchStartY));
        } else if (event.getPointerCount() == 2) {
            secondTouchStartX = event.getX(1);
            secondTouchStartY = event.getY(1);

            pinchStartDistance = getPinchDistance(event);

            Log.d("GestureBuilder", String.format("POINTER TWO DOWN X = %.5f, Y = %.5f", secondTouchStartX, secondTouchStartY));
        }
    }

    private void addDoubleTouchMoveEvent(MotionEvent event) {
        // First detect pinch, then detect scroll.  Final possibility is rotation.
        if (pinchExceedsSlopThreshold(event)) {
            // Touches are pinching together or apart.
            Log.d("GestureBuilder", String.format("PINCH: %.5f", getPinchDistance(event) - pinchStartDistance));

            finalGestureType = MotionType.PINCH;
            finalGestureValue = getPinchDistance(event) - pinchStartDistance;
        } else if (centreMotionExceedsSlopThreshold(event)) {
            // Touches are moving together in some direction, but only execute on vertical motion.
            if (verticalMotionExceedsSlopThreshold(event)) {
                Log.d("GestureBuilder", String.format("VERTICAL_SCROLL: %.5f", getVerticalCentreMotion(event)));

                finalGestureType = MotionType.VERTICAL_SCROLL;
                finalGestureValue = getVerticalCentreMotion(event);
            }
        } else {
            Log.d("GestureBuilder", String.format("ROTATE: %.5f", getRotationAngle(event)));

            finalGestureType = MotionType.ROTATE;
            finalGestureValue = getRotationAngle(event);
        }
    }

    ///////
    // MOTION SLOP THRESHOLD DETECTORS
    ///////



    private boolean motionExceedsSlopThreshold(MotionEvent event, int touchIndex, float startX, float startY) {
        if (touchIndex < event.getPointerCount()) {
            float motionX = Math.abs(event.getX(touchIndex) - startX);
            float motionY = Math.abs(event.getY(touchIndex) - startY);

            if (motionX > touchSlop || motionY > touchSlop) {
                return true;
            }
        }
        return false;
    }

    private boolean pinchExceedsSlopThreshold(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float currentPinchDistance = getPinchDistance(event);

            // Minor pinching is to be expected during other swipes.  Widen the slop threshold.
            if (Math.abs(currentPinchDistance - pinchStartDistance) > touchSlop * 5) {
                return true;
            }
        }

        return false;
    }

    private boolean centreMotionExceedsSlopThreshold(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float centreMotion = getCentreMotion(event);

            // Controlling centre of rotating fingers is hard.  Widen the slop threshold.
            if (centreMotion > touchSlop * 7.5) {
                return true;
            }
        }

        return false;
    }

    private boolean verticalMotionExceedsSlopThreshold(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float verticalCentreMotion = getVerticalCentreMotion(event);

            if (Math.abs(verticalCentreMotion) > touchSlop * 5) {
                return true;
            }
        }

        return false;
    }

    ///////
    // MOTION AMOUNT DETECTORS
    ///////

    private float getPinchDistance(MotionEvent event) {
        if (event.getPointerCount() >= 2) {
            float deltaX = event.getX(0) - event.getX(1);
            float deltaY = event.getY(0) - event.getY(1);

            return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }

        return 0;
    }

    private float getRotationAngle(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float firstTouchCurrentX = event.getX(0);
            float secondTouchCurrentX = event.getX(1);

            float firstTouchCurrentY = event.getY(0);
            float secondTouchCurrentY = event.getY(1);

            double currentAngle = Math.atan2(firstTouchCurrentY - secondTouchCurrentY, firstTouchCurrentX - secondTouchCurrentX);
            double startAngle = Math.atan2(firstTouchStartY - secondTouchStartY, firstTouchStartX - secondTouchStartX);

            double rotationAngle = (currentAngle - startAngle) % 2 * Math.PI;

            if (rotationAngle < -1 * Math.PI) rotationAngle += 2 * Math.PI;
            if (rotationAngle > Math.PI) rotationAngle -= 2 * Math.PI;

            return (float) rotationAngle;
        }

        return 0;
    }

    private float getCentreMotion(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float centreX = (event.getX(0) + event.getX(1)) / 2f;
            float centreY = (event.getY(0) + event.getY(1)) / 2f;

            float startCentreX = (firstTouchStartX + secondTouchStartX) / 2f;
            float startCentreY = (firstTouchStartY + secondTouchStartY) / 2f;

            float deltaX = centreX - startCentreX;
            float deltaY = centreY - startCentreY;

            return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }

        return 0;
    }

    private float getVerticalCentreMotion(MotionEvent event) {
        if (event.getPointerCount() == 2) {

            float centreY = (event.getY(0) + event.getY(1)) / 2f;
            float startCentreY = (firstTouchStartY + secondTouchStartY) / 2f;

            return  startCentreY - centreY;
        }

        return 0;
    }
}

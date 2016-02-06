package ece493.kdbanman.Gesture;

/**
 * Created by kdban on 2/5/2016.
 */
public class Gesture {

    float gestureValue;
    GestureCallback gestureCallback;

    public Gesture(float value, GestureCallback callback) {
        gestureValue = value;
        gestureCallback = callback;
    }

    public void execute() {
        gestureCallback.executeGesture(gestureValue);
    }
}

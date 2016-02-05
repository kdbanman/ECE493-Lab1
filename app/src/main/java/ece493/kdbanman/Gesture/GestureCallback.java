package ece493.kdbanman.Gesture;

/**
 * Created by kdban on 2/5/2016.
 */
public interface GestureCallback {
    void executeGesture(float gestureValue, int[] argbPixels, int width, int height);
}

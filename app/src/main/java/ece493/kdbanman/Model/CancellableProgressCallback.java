package ece493.kdbanman.Model;

/**
 * Simple callback
 * Created by kdban on 1/17/2016.
 */
public interface CancellableProgressCallback {
    void onProgressUpdate(int percentDone);
    boolean isCancelled();
}

package ece493.kdbanman.Model;

/**
 * Simple callback for a task that can report integer progress and report cancellation status.
 *
 * Created by kdbanman on 1/17/2016.
 */
public interface TaskProgressCallback {
    void onProgressUpdate(int percentDone);
    boolean isCancelled();
}

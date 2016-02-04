package ece493.kdbanman.Model;

import android.os.AsyncTask;
import android.util.Log;

import ece493.kdbanman.Observable;

/**
 * Cancellable task wrapping Filterable.applyFilter() that only notifies observers on
 * the foreground thread.
 *
 * Not accessible outside of Model package.
 *
 * Created by kdbanman on 1/15/16.
 */
class BackgroundFilterTask extends AsyncTask<Filterable, Integer, int[]> {

    private boolean taskRunning;
    private boolean cancelTask;
    private boolean taskComplete;

    private int progress;

    private FilterKernel filterKernel;

    private Filterable image;
    private int[] filteredPixels;

    private Observable indirectObservable;



    public BackgroundFilterTask(FilterKernel filterKernel, Observable indirectObservable) {
        this.filterKernel = filterKernel;
        this.indirectObservable = indirectObservable;
    }



    public boolean isTaskRunning() {
        return taskRunning;
    }

    public boolean isTaskCancelled() {
        return cancelTask;
    }

    public boolean isTaskComplete() {
        return taskComplete;
    }

    public int getProgress() {
        return progress;
    }

    public void cancelTask() {
        this.cancelTask = true;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("BackgroundFilterTask", "Task started.");

        taskRunning = true;
        cancelTask = false;
        taskComplete = false;

        progress = 0;

        indirectObservable.notifyObservers();
    }

    @Override
    protected int[] doInBackground(Filterable... params) {
        // Do NOT call notifyObservers() directly or indirectly in this method.
        try {
            image = params[0];
            filteredPixels = image.applyFilter(filterKernel, new TaskProgressCallback() {
                @Override
                public void onProgressUpdate(int percentDone) {
                    if (progress == percentDone) {
                        return;
                    }

                    progress = percentDone;
                    publishProgress();
                }

                @Override
                public boolean isCancelled() {
                    return cancelTask;
                }
            });

            return filteredPixels;
        } catch (OutOfMemoryError e) {
            Log.e("BackgroundFilterTask", "Insufficient memory to filter image.");
            return null;
        } catch (Exception e) {
            Log.e("BackgroundFilterTask", e.toString());
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        indirectObservable.notifyObservers();
    }

    @Override
    protected void onPostExecute(int[] result) {
        super.onPostExecute(result);
        Log.v("BackgroundFilterTask", "Task done.");

        if (image != null && filteredPixels != null && !cancelTask) {
            image.setPixels(filteredPixels);
        }

        taskRunning = false;
        taskComplete = true;
        indirectObservable.notifyObservers();
    }
}
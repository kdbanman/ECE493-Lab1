package ece493.kdbanman.Model;

import android.os.AsyncTask;
import android.util.Log;

import ece493.kdbanman.Model.FilterKernel;
import ece493.kdbanman.Model.Filterable;
import ece493.kdbanman.Observable;

/**
 * Cancellable task wrapping Filterable.getProcessedPixels() that only notifies observers on
 * the foreground thread.
 *
 * Created by kdbanman on 1/15/16.
 */
public class BackgroundFilterTask extends AsyncTask<Filterable, Void, int[]> {

    private boolean taskRunning;
    private boolean cancelTask;

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

    public void cancelTask() {
        this.cancelTask = true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("BackgroundFilterTask", "Task started.");

        taskRunning = true;
        cancelTask = false;
        indirectObservable.notifyObservers();
    }

    @Override
    protected int[] doInBackground(Filterable... params) {
        // Do not call notifyObservers() directly or indirectly in this method.
        try {
            image = params[0];
            filteredPixels = params[0].getProcessedPixels(filterKernel);

            return filteredPixels;
        } catch (Exception e) {
            Log.e("BackgroundFilterTask", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(int[] result) {
        super.onPostExecute(result);
        Log.v("BackgroundFilterTask", "Task done.");

        if (image != null && filteredPixels != null && !cancelTask) {
            image.setPixels(filteredPixels);
        }

        taskRunning = false;
        indirectObservable.notifyObservers();
    }
}
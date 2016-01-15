package ece493.kdbanman.Model;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ece493.kdbanman.Observable;

/**
 * Implementation of the logic and state that is common to kernel-based convolution
 * filters.  Works with the Filterable class and FilterKernel interface.
 *
 * Created by kdbanman on 1/13/16.
 */
public class ImageFilter extends Observable {

    private int kernelSize = 3;

    private FilterKernelType filterKernelType = FilterKernelType.mean;

    private List<BackgroundFilterTask> backgroundTasks = new ArrayList<>();

    public boolean isFilterRunning() {
        for (BackgroundFilterTask task : backgroundTasks) {
            if (task.isTaskRunning()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTaskStopping() {
        for (BackgroundFilterTask task : backgroundTasks) {
            if (task.isTaskRunning() && task.isTaskCancelled()) {
                return true;
            }
        }
        return false;
    }

    public void cancelBackgroundFilterTasks() {
        for (BackgroundFilterTask task : backgroundTasks) {
            task.cancelTask();
        }
    }

    public void setKernelType(FilterKernelType type) {
        filterKernelType = type;

        notifyObservers();
    }

    public FilterKernelType getKernelType() {
        return filterKernelType;
    }

    /**
     * @return The edge length in pixels for the square convolution window.
     */
    public final int getKernelSize() {
        return kernelSize;
    }

    /**
     * Sets the edge length in pixels for the square convolution window.
     *
     * @param kernelSize The new convolution window size.
     * @throws IllegalArgumentException if the kernelSize is less than 3 or odd.
     */
    public final void setKernelSize(int kernelSize) {
        if (kernelSize < 3) {
            throw new IllegalArgumentException("Kernel size must be 3 or larger.");
        }
        if (kernelSize % 2 == 0) {
            throw new IllegalArgumentException("Kernel size must be odd.");
        }

        this.kernelSize = kernelSize;

        notifyObservers();
    }

    /**
     * Applies a convolution filter to a filterable image in a background task.
     *
     * @param image The image to be filtered.
     */
    public void backgroundFilterImage(Filterable image) {
        BackgroundFilterTask task = new BackgroundFilterTask();
        backgroundTasks.add(task);
        task.execute(image);
    }

    /**
     * Cancellable task wrapping Filterable.getProcessedPixels() that only notifies observers on
     * the foreground thread.
     */
    private class BackgroundFilterTask extends AsyncTask<Filterable, Void, Boolean> {

        private boolean taskRunning;
        private boolean cancelTask;

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
            notifyObservers();
        }

        @Override
        protected Boolean doInBackground(Filterable... params) {
            // Do not call notifyObservers() in this method.
            int j = 0;
            for (int i = 0; i < 200000; i++) {
                j = new java.util.Random().nextInt();

                if (cancelTask) {
                    Log.v("BackgroundFilterTask", "Task cancel flag detected.");

                    cancelTask = false;
                    return false;
                }
            }

            // whether by logic here or within the callack, ensure filterable is not mutated until here
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.v("BackgroundFilterTask", "Task done with result " + result);

            taskRunning = false;
            notifyObservers();
        }
    }
}

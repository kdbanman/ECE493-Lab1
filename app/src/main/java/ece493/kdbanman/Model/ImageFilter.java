package ece493.kdbanman.Model;

import java.util.ArrayList;
import java.util.List;

import ece493.kdbanman.Observable;

/**
 * Logic and state that is to run kernel-based convolution filters on a background thread.
 * Works with the Filterable class and FilterKernel interface.
 *
 * Accessible outside of Model package, but not instantiable outside of Model package.
 * Instantiated and served by ModelServer.
 *
 * Created by kdbanman on 1/13/16.
 */
public class ImageFilter extends Observable {

    private int kernelSize = 3;

    private FilterKernelType filterKernelType = FilterKernelType.MEAN;

    private List<BackgroundFilterTask> backgroundTasks = new ArrayList<>();

    protected ImageFilter() {}


    /**
     * Clear the list of background tasks if they have finished (from cancellation *or* completion).
     */
    private void purgeOldTasks() {
        List<BackgroundFilterTask> toRemove = new ArrayList<>();
        for (BackgroundFilterTask task : backgroundTasks) {
            if (task.isTaskComplete()) {
                toRemove.add(task);
            }
        }

        backgroundTasks.removeAll(toRemove);
    }

    /**
     * @return True if there is at least one background task running.
     */
    public boolean isFilterRunning() {
        for (BackgroundFilterTask task : backgroundTasks) {
            if (task.isTaskRunning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * More than one currently running tasks means the minimum progress is returned.
     *
     * @return The progress of the currently running task between 1 and 100.
     */
    public int getTaskProgress() {
        purgeOldTasks();
        int minProgress = 100;
        for (BackgroundFilterTask task : backgroundTasks) {
            minProgress = Math.min(minProgress, task.getProgress());
        }
        return minProgress;
    }

    /**
     * @return True if there is one or more background tasks currently running, but whose
     * cancellation has been requested.
     */
    public boolean isTaskStopping() {
        for (BackgroundFilterTask task : backgroundTasks) {
            if (task.isTaskRunning() && task.isTaskCancelled()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Request cancellation of all image filters currently running in the background. (Asynchronous)
     */
    public void cancelBackgroundFilterTasks() {
        for (BackgroundFilterTask task : backgroundTasks) {
            task.cancelTask();
        }
    }

    /**
     * @param type The type of filter that will be applied when filterImageInBackground() is called.
     */
    public void setKernelType(FilterKernelType type) {
        filterKernelType = type;

        notifyObservers();
    }

    /**
     * @return The type of filter that will be applied when filterImageInBackground() is called.
     */
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
    public void filterImageInBackground(Filterable image) {
        purgeOldTasks();

        if (image == null) {
            throw new IllegalArgumentException("Image must exist to be filtered.");
        }

        FilterKernel filterKernel;
        switch (filterKernelType) {
            case MEAN:
                filterKernel = new MeanFilterKernel(kernelSize);
                break;
            case MEDIAN:
                filterKernel = new MedianFilterKernel(kernelSize);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized filter kernel type requested in background task.");
        }

        BackgroundFilterTask task = new BackgroundFilterTask(filterKernel, this);
        backgroundTasks.add(task);


        task.execute(image);
    }
}

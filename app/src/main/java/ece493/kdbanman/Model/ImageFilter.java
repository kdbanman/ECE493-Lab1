package ece493.kdbanman.Model;

import ece493.kdbanman.Observable;

/**
 * Implementation of the logic and state that is common to kernel-based convolution
 * filters.  Works with the Filterable class and FilterKernel interface.
 *
 * Created by kdbanman on 1/13/16.
 */
public class ImageFilter extends Observable {

    private int kernelSize;

    private FilterKernelType filterKernelType = FilterKernelType.mean;

    private boolean filterRunning = false;

    /**
     * Calls notifyObservers().
     * @param isRunning New running state.
     */
    private void setFilterRunning(boolean isRunning) {
        filterRunning = isRunning;
        notifyObservers();
    }

    public boolean isFilterRunning() {
        return filterRunning;
    }

    public void cancelBackgroundFilterTask() {
        //TODO

        notifyObservers();
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
        setFilterRunning(true);
        // TODO cancellable task wrapping Filterable.processPixels() or whatever, remember setFilterRunning()
    }
}

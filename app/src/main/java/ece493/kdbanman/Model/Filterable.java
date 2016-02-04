package ece493.kdbanman.Model;

import android.graphics.Bitmap;
import android.util.Size;

import ece493.kdbanman.Observable;

/**
 * An observable Bitmap wrapper with forwarding and facade methods to accessors and
 * mutators that are useful for display and convolution filtering.
 *
 * Accessible outside of Model package, but not instantiable outside of Model package.
 * Instantiated and served by ModelServer.
 *
 * Created by kdbanman on 1/13/16.
 */
public class Filterable extends Observable {

    private Bitmap image;

    protected Filterable() {}



    // ===================
    // Public Methods

    /**
     * Give the Filterable a new image to filter.
     *
     * @param image The image.  (Set by reference, do not call .recycle())
     */
    public void setImage(Bitmap image) {
        if (this.image != null && !this.image.isRecycled()) {
            this.image.recycle();
        }

        this.image = image;

        notifyObservers();
    }

    /**
     * @return True if the Filterable has an image to filter.
     */
    public boolean hasImage() {
        return image != null;
    }

    /**
     * @param maxHeight The maximum allowable height.
     * @param maxWidth The maximum allowable width.
     * @return A scaled *copy* of the image that obeys the passed max width and max height, but
     * preserves the aspect ratio.
     */
    public Bitmap getScaledCopy(int maxHeight, int maxWidth) {
        if (image == null) {
            return null;
        }

        Size copySize = computeScaleDimensions(maxHeight, maxWidth);

        return Bitmap.createScaledBitmap(image, copySize.getWidth(), copySize.getHeight(), false);
    }

    /**
     * Modify the current image by setting its pixel array.
     *
     * @throws IllegalArgumentException if the pixels array is null or not compatible with the
     *          current image.
     * @param pixels The new pixel array for the current image.
     */
    public void setPixels(int[] pixels) {
        if (image == null) {
            throw new IllegalArgumentException("Image must be set before Filterable.setPixels() may be called");
        }
        if (pixels.length != image.getHeight() * image.getWidth()) {
            throw new IllegalArgumentException("Filterable.setPixels() must be called with an array of the correct length.");
        }

        image.setPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        notifyObservers();
    }

    /**
     * @return The width of the current image.
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * @return The height of the current image.
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Using a kernel window callback, get a transformed copy of the image's pixels.  Pixels outside
     * the image are treated as if they had the same value as their nearest edge pixel.
     *
     * @param filterKernel The callback that will process the neighborhoods filtered by the kernel.
     * @throws IllegalArgumentException If the filterKernel has an invalid size.
     * @return A copy of the pixels as processed by the filterKernel
     */
    public int[] applyFilter(FilterKernel filterKernel, TaskProgressCallback progressCallback) {
        int neighborhoodSize = filterKernel.getSize();
        if (neighborhoodSize < 3 || neighborhoodSize % 2 == 0) {
            throw new IllegalArgumentException("Filterable.applyFilter() called with bad neighborhood size.");
        }

        int[] sourcePixels = getPixels();
        int[] filteredPixels = new int[sourcePixels.length];
        byte[] neighborhood = new byte[neighborhoodSize * neighborhoodSize];

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int neighborhoodRadius = neighborhoodSize / 2;
        for (int col = 0; col < imageWidth; col++) {
            // Process an entire row of the image
            for (int row = 0; row < imageHeight; row++) {
                int filteredPixel = getFilteredPixel(filterKernel, sourcePixels, neighborhood, imageWidth, imageHeight, neighborhoodRadius, col, row);
                filteredPixels[row * imageWidth + col] = filteredPixel;
            }

            // Stop and return null if cancelled, report current progress if not.
            if (progressCallback.isCancelled()) {
                return null;
            }
            progressCallback.onProgressUpdate((100 * col) / imageWidth);
        }

        return filteredPixels;
    }

    /**
     * Applies a filter kernel to a particular pixel.
     *
     * @param filterKernel Filter to apply.
     * @param sourcePixels Image to filter.
     * @param neighborhood Array to use for the pixel neighbors. (Passed in to reduce array allocations).
     * @param imageWidth The image's width.
     * @param imageHeight The image's height.
     * @param neighborhoodRadius The radius of the neighborhood.
     * @param col The column of the pixel to filter.
     * @param row The row of the pixel to filter.
     * @return The ARGB color integer that is the result of the filter.
     */
    private int getFilteredPixel(FilterKernel filterKernel, int[] sourcePixels, byte[] neighborhood, int imageWidth, int imageHeight, int neighborhoodRadius, int col, int row) {
        int filteredPixel = 0;

        for (FilterableChannel channel : FilterableChannel.values()) {
            // Build neighborhood.
            int neighborIndex = 0;
            for (int neighborRow = row - neighborhoodRadius; neighborRow <= row + neighborhoodRadius; neighborRow++) {
                for (int neighborCol = col - neighborhoodRadius; neighborCol <= col + neighborhoodRadius; neighborCol++) {
                    // Clamp neighbor coordinates to within image bounds
                    int clampedRow = Math.min(imageHeight - 1, Math.max(0, neighborRow));
                    int clampedCol = Math.min(imageWidth - 1, Math.max(0, neighborCol));

                    int sourcePixelColor = sourcePixels[clampedRow * imageWidth + clampedCol];
                    byte channelColor = channel.getValue(sourcePixelColor);
                    neighborhood[neighborIndex] = channelColor;

                    neighborIndex++;
                }
            }
            byte filteredChannelPixel = filterKernel.processNeighborhood(neighborhood);
            filteredPixel = channel.setValue(filteredChannelPixel, filteredPixel);
        }
        return filteredPixel;
    }



    // =================
    // Private Methods

    /*
     * Given a maximum width and height, compute an aspect-ratio-preserving new Size.
     */
    private Size computeScaleDimensions(int maxHeight, int maxWidth) {
        double imageAspectRatio = (double)image.getWidth() / (double)image.getHeight();

        int scaledWidth, scaledHeight;
        if (maxWidth > maxHeight * imageAspectRatio) {
            scaledHeight = maxHeight;
            scaledWidth = (int)(maxHeight * imageAspectRatio);
        } else {
            scaledHeight = (int)(maxWidth / imageAspectRatio);
            scaledWidth = maxWidth;
        }

        return new Size(scaledWidth, scaledHeight);
    }

    /*
     * Get a *copy* of the pixel array within the current image.
     */
    private int[] getPixels() {
        if (image == null) {
            throw new IllegalArgumentException("Image must be set before Filterable.getPixels() may be called");
        }

        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        return pixels;
    }
}

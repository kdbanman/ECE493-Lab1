package ece493.kdbanman.Model;

import android.graphics.Bitmap;
import android.util.Size;

import ece493.kdbanman.Observable;

/**
 * An observable Bitmap wrapper with forwarding and facade methods to accessors and
 * mutators that are useful for display and convolution filtering.
 *
 * Created by kdbanman on 1/13/16.
 */
public class Filterable extends Observable {

    private Bitmap image;

    protected Filterable() {}

    // =================
    // Private Methods

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

    private int[] getPixels() {
        if (image == null) {
            throw new IllegalArgumentException("Image must be set before Filterable.getPixels() may be called");
        }

        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        return pixels;
    }

    // ===================
    // Public Methods

    public Bitmap getScaledCopy(int maxHeight, int maxWidth) {
        if (image == null) {
            return null;
        }

        Size copySize = computeScaleDimensions(maxHeight, maxWidth);

        return Bitmap.createScaledBitmap(image, copySize.getWidth(), copySize.getHeight(), false);
    }

    public void setImage(Bitmap image) {
        if (this.image != null && !this.image.isRecycled()) {
            this.image.recycle();
        }

        this.image = image;

        notifyObservers();
    }

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

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Using a kernel window callback, get a transformed copy of the image's pixels.  Pixels outside
     * the image are treated as if they had the same value as their nearest edge pixel.
     *
     * @param filterKernel The callback that will process the neighborhoods filtered by the kernel.
     * @return A copy of the pixels as processed by the filterKernel
     */
    public int[] applyFilter(FilterKernel filterKernel) {
        int neighborhoodSize = filterKernel.getSize();
        if (neighborhoodSize < 3 || neighborhoodSize % 2 == 0) {
            throw new IllegalArgumentException("Filterable.applyFilter() called with bad neighborhood size.");
        }

        int[] sourcePixels = getPixels();
        int[] targetPixels = new int[sourcePixels.length];
        byte[] neighborhood = new byte[neighborhoodSize * neighborhoodSize];

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int neighborhoodRadius = neighborhoodSize / 2;
        for (int col = 0; col < imageWidth; col++) {
            for (int row = 0; row < imageHeight; row++) {

                int targetPixel = 0;

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
                    targetPixel = channel.setValue(filteredChannelPixel, targetPixel);
                }

                targetPixels[row * imageWidth + col] = targetPixel;
            }
        }

        return targetPixels;
    }

    public boolean hasImage() {
        return image != null;
    }
}

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

    // =================
    // Private

    private Bitmap image;

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
        image.getPixels(pixels, 0, 0, 0, 0, image.getWidth(), image.getHeight());
        return pixels;
    }

    // ===================
    // Public

    public Bitmap getScaledCopy(int maxHeight, int maxWidth) {
        if (image == null) {
            return null;
        }

        Size copySize = computeScaleDimensions(maxHeight, maxWidth);

        return Bitmap.createScaledBitmap(image, copySize.getWidth(), copySize.getHeight(), false);
    }

    public void setImage(Bitmap image) {
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

        image.setPixels(pixels, 0, 0, 0, 0, image.getWidth(), image.getHeight());

        notifyObservers();
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int[] getProcessedPixels(int neighborhoodSize, FilterKernel filterKernel) {
        int[] sourcePixels = getPixels();
        int[] targetPixels = new int[sourcePixels.length];
        byte[] neighborhood = new byte[neighborhoodSize * neighborhoodSize];

        int width = image.getWidth();
        int height = image.getHeight();
        for (int col = 0; col < image.getWidth(); col++) {
            for (int row = 0; row < image.getHeight(); row++) {
                int targetPixel = 0;
                // TODO:
                // for each channel (mask)
                    // for each nbrhood coordinate
                        // extract byte value into nbrhood using mask
                    // byte processedPixel = filterKernel.processNeighborhood(row, col, neighborhood);
                    // shift to align with channel (mask)
                    // targetPixel |= shifted byte value
                targetPixels[row * width + col] = targetPixel;
            }
        }

        return targetPixels;
    }

    public boolean hasImage() {
        return image != null;
    }
}

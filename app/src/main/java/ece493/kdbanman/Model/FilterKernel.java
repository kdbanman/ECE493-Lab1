package ece493.kdbanman.Model;

/**
 * A lambda-style callback for processing a kernel-defined neighborhood of pixels.
 *
 * Not accessible outside of Model package.
 *
 * Created by kdbanman on 1/13/16.
 */
abstract class FilterKernel {

    private int size = 3;

    public FilterKernel(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    /**
     * Process a kernel-defined neighborhood of pixels.
     * The callback may be passed a neighborhood from any channel (A, R, G, or B).
     *
     * @param neighborhood The byte values of the neighborhood.
     * @return The new value of the pixel.
     */
    public abstract byte processNeighborhood(byte[] neighborhood);
}

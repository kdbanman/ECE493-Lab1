package ece493.kdbanman.Model;

/**
 * A lambda-style callback for processing a kernel-defined neighborhood of pixels.
 * Created by kdbanman on 1/13/16.
 */
public interface FilterKernel {

    /**
     * Process a kernel-defined neighborhood of pixels centered at the row and column passed.
     * The callback may be passed a neighborhood from any channel (A, R, G, or B).
     *
     * @param row The central row of the neighborhood.
     * @param col The central column of the neighborhood.
     * @param neighborhood The byte values of the neighborhood.
     * @return The new value of the pixel.
     */
    byte processNeighborhood(int row, int col, byte[] neighborhood);
}

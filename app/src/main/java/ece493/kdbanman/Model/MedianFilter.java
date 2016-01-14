package ece493.kdbanman.Model;

/**
 * An convolutional median filter.
 *
 * Created by kdbanman on 1/13/16.
 */
public class MedianFilter implements FilterKernel {
    @Override
    public byte processNeighborhood(int row, int col, byte[] neighborhood) {
        // TODO compute and return mean
        return 0;
    }
}

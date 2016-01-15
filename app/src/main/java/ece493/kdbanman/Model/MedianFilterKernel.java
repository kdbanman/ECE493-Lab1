package ece493.kdbanman.Model;

/**
 * An convolutional median filter.
 *
 * Created by kdbanman on 1/13/16.
 */
public class MedianFilterKernel extends FilterKernel {

    public MedianFilterKernel(int size) { super(size); }

    @Override
    public byte processNeighborhood(int row, int col, byte[] neighborhood) {
        // TODO compute and return mean
        return 0;
    }
}

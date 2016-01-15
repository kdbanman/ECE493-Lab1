package ece493.kdbanman.Model;

/**
 * An convolutional MEDIAN filter.
 *
 * Created by kdbanman on 1/13/16.
 */
class MedianFilterKernel extends FilterKernel {

    public MedianFilterKernel(int size) { super(size); }

    @Override
    public byte processNeighborhood(byte[] neighborhood) {
        // TODO compute and return MEAN
        return 0;
    }
}

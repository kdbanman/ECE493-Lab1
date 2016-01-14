package ece493.kdbanman.Model;

/**
 * An convolutional mean filter.
 *
 * Created by kdbanman on 1/13/16.
 */
public class MeanFilter implements FilterKernel {

    @Override
    public byte processNeighborhood(int row, int col, byte[] neighborhood) {
        // TODO compute and return mean
        return 0;
    }
}

package ece493.kdbanman.Model;

/**
 * An convolutional mean filter.
 *
 * Created by kdbanman on 1/13/16.
 */
public class MeanFilterKernel extends FilterKernel {

    public MeanFilterKernel(int size) { super(size); }

    @Override
    public byte processNeighborhood(int row, int col, byte[] neighborhood) {
        int sum = 0;
        for (byte aNeighborhood : neighborhood) {
            sum += aNeighborhood;
        }
        return (byte)(sum / neighborhood.length);
    }
}

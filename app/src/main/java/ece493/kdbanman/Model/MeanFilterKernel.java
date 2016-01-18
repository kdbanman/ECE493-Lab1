package ece493.kdbanman.Model;

/**
 * An MEAN convolution filter.
 *
 * Not accessible outside of Model package.
 *
 * Created by kdbanman on 1/13/16.
 */
class MeanFilterKernel extends FilterKernel {

    public MeanFilterKernel(int size) { super(size); }

    @Override
    public byte processNeighborhood(byte[] neighborhood) {
        int sum = 0;
        for (byte value : neighborhood) {
            sum += value & 0xFF;
        }
        return (byte)(sum / neighborhood.length);
    }
}

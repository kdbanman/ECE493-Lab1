package ece493.kdbanman.Model;

import java.util.Random;

/**
 * An convolutional MEDIAN filter.  See https://en.wikipedia.org/wiki/Quickselect for implementation
 * reference.
 *
 * Created by kdbanman on 1/13/16.
 */
class MedianFilterKernel extends FilterKernel {

    private Random random = new Random();

    public MedianFilterKernel(int size) { super(size); }

    @Override
    public byte processNeighborhood(byte[] neighborhood) {
        return getMedian(neighborhood);
    }

    private byte getMedian(byte[] bytes) {
        return selectMedian(bytes, 0, bytes.length - 1);
    }

    /**
     * Select the median of an array using Hoare's recursive method (see: quicksort), treating bytes
     * as if they were unsigned.
     *
     * @param bytes The array to select from.
     * @param leftBound The left index of the space to select from.
     * @param rightBound The right index of the space to select from.
     * @return The value of the median.
     */
    private byte selectMedian(byte[] bytes, int leftBound, int rightBound) {
        if (leftBound == rightBound) {
            return bytes[leftBound];
        }
        int pivotIndex = random.nextInt(rightBound - leftBound) + leftBound;
        pivotIndex = partition(bytes, leftBound, rightBound, pivotIndex);

        if (pivotIndex == bytes.length / 2) {
            return bytes[pivotIndex];
        }
        else if (pivotIndex > bytes.length / 2) {
            return selectMedian(bytes, leftBound, pivotIndex - 1);
        }
        else {
            return selectMedian(bytes, pivotIndex + 1, rightBound);
        }
    }

    /**
     * Partition an array of bytes about the pivot into greater and lesser elements, treating bytes
     * as if they were unsigned.
     *
     * @param bytes The array to partition.
     * @param leftBound The left index of the space to partition.
     * @param rightBound The right index of the space to partition.
     * @param pivotIndex The index about which to partition.
     * @return The "sorted position" of the value at the pivot.
     */
    private int partition(byte[] bytes, int leftBound, int rightBound, int pivotIndex) {
        byte pivotValue = bytes[pivotIndex];
        bytes[pivotIndex] = bytes[rightBound];
        bytes[rightBound] = pivotValue;

        int trailingIndex = leftBound;
        for (int leadIndex = leftBound; leadIndex < rightBound; leadIndex++) {
            if (unsignedLessThan(bytes[leadIndex], pivotValue)) {
                byte swap = bytes[trailingIndex];
                bytes[trailingIndex] = bytes[leadIndex];
                bytes[leadIndex] = swap;

                trailingIndex++;
            }
        }

        byte swap = bytes[trailingIndex];
        bytes[trailingIndex] = bytes[rightBound];
        bytes[rightBound] = swap;

        return trailingIndex;
    }

    private boolean unsignedLessThan(byte left, byte right) {
        return (((int)left) & 0xFF) < (((int)right) & 0xFF);
    }
}

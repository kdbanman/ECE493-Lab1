package ece493.kdbanman.Model;

import java.util.Arrays;
import java.util.Random;

/**
 * An MEDIAN convolution filter.  See https://en.wikipedia.org/wiki/Quickselect for implementation
 * reference.
 *
 * Not accessible outside of Model package.
 *
 * Created by kdbanman on 1/13/16.
 */
class MedianFilterKernel extends FilterKernel {

    // Java bytes are signed, but the values extracted from the colors are not.  This array is used
    // to convert the bytes into positive integers for correct sorting order.
    private int[] sortableByteValues;

    private Random random = new Random();

    public MedianFilterKernel(int size) {
        super(size);

        sortableByteValues = new int[size * size];
    }

    @Override
    public byte processNeighborhood(byte[] neighborhood) {
        if (neighborhood.length != sortableByteValues.length) {
            throw new IllegalArgumentException("MedianFilterKernel: passed neighborhood size is not equal to initialization size.");
        }

        for (int i = 0; i < neighborhood.length; i++) {
            sortableByteValues[i] = neighborhood[i] & 0xFF;
        }

        Arrays.sort(sortableByteValues, 0, sortableByteValues.length);

        return (byte)sortableByteValues[sortableByteValues.length / 2];
    }
}

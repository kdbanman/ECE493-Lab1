package ece493.kdbanman.Model;

/**
 * Enumeration of the channels packed into a bitmap integer.
 * Created by kdbanman on 1/13/16.
 */
public enum FilterableChannel {
    ALPHA, RED, GREEN, BLUE;

    private int colorMask;
    private int bitDisplacement;

    static {
        ALPHA.colorMask = 0xFF000000;
        RED.colorMask   = 0x00FF0000;
        GREEN.colorMask = 0x0000FF00;
        BLUE.colorMask  = 0x000000FF;

        ALPHA.bitDisplacement   = 24;
        RED.bitDisplacement     = 16;
        GREEN.bitDisplacement   = 8;
        BLUE.bitDisplacement    = 0;
    }

    public byte getValue(int color) {
        return (byte)((color & colorMask) >> bitDisplacement);
    }

    public int setValue(byte value, int color) {
        color &= ~colorMask;
        return color | (((int)value) << bitDisplacement);
    }
}

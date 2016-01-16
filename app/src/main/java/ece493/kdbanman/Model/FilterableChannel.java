package ece493.kdbanman.Model;

/**
 * Enumeration of the channels packed into a bitmap integer.
 * Created by kdbanman on 1/13/16.
 */
enum FilterableChannel {
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
        int masked = color & colorMask;
        int shifted = masked >>> bitDisplacement;
        byte casted = (byte)shifted;
        return casted;
    }

    public int setValue(byte value, int color) {
        int castedValue = (int)value & 0xFF;
        int shiftedValue = castedValue << bitDisplacement;

        int inverseMask = ~colorMask;
        int clearedColor = color & inverseMask;

        int newColor = clearedColor | shiftedValue;

        return newColor;
    }
}

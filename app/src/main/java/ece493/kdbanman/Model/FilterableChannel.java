package ece493.kdbanman.Model;

/**
 * Enumeration of the channels packed into a bitmap integer with byte-level getters and setters
 * for integer color values.
 *
 * Not accessible outside of Model package.
 *
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

    /**
     * Get the channel value from a color.
     *
     * @param color The ARGB color to extract from.
     * @return The byte value of the channel for the passed color.
     */
    public byte getValue(int color) {
        int masked = color & colorMask;
        int shifted = masked >>> bitDisplacement;
        return (byte)shifted;
    }

    /**
     * Set a channel value for a color and return it.
     *
     * @param value The byte value for the channel.
     * @param color The ARGB color to "modify" (recall: Java is pass-by-value)
     * @return The passed ARGB color with the new channel value.
     */
    public int setValue(byte value, int color) {
        int castedValue = (int)value & 0xFF;
        int shiftedValue = castedValue << bitDisplacement;

        int inverseMask = ~colorMask;
        int clearedColor = color & inverseMask;

        return clearedColor | shiftedValue;
    }
}

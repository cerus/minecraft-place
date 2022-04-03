package dev.cerus.minecraftplace.canvas;

import java.awt.Color;

/**
 * Represents a canvas. Pixels are stored in a simple array of color indexes.
 */
public class Canvas {

    private final Palette palette;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final byte[] data;

    public Canvas(final Palette palette, final int x, final int y, final int width, final int height) {
        this.palette = palette;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.data = new byte[width * height];
    }

    /**
     * Set the pixel at the specified coordinates to the specified color
     *
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param color The color
     */
    public void setPixel(final int x, final int y, final Color color) {
        this.data[this.index(x, y)] = (byte) this.palette.getIndex(color);
    }

    /**
     * Get a pixel at the specified coordinates
     *
     * @param x The x coordinate
     * @param y The y coordinate
     *
     * @return The pixel
     */
    public Color getPixel(final int x, final int y) {
        return this.palette.getColor(this.data[this.index(x, y)]);
    }

    /**
     * x & y to array index
     *
     * @param x X coordinate
     * @param y Y coordinate
     *
     * @return Array index
     */
    private int index(final int x, final int y) {
        return x + y * Math.max(this.width, this.height);
    }

    public byte[] getData() {
        return this.data;
    }

    public Palette getPalette() {
        return this.palette;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

}
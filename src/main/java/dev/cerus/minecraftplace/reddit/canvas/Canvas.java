package dev.cerus.minecraftplace.reddit.canvas;

import dev.cerus.maps.api.graphics.ColorCache;
import dev.cerus.maps.api.graphics.MapGraphics;
import dev.cerus.maps.api.graphics.StandaloneMapGraphics;
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
    private final MapGraphics<?, ?> data;

    public Canvas(final Palette palette, final int x, final int y, final int width, final int height) {
        this.palette = palette;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.data = StandaloneMapGraphics.standalone(width, height);
    }

    /**
     * Set the pixel at the specified coordinates to the specified color
     *
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param color The color
     * @param force Ignore palette restrictions
     */
    public void setPixel(final int x, final int y, final Color color, final boolean force) {
        /*this.data[this.index(x, y)] = force
                ? (ColorCache.rgbToMap(color.getRed(), color.getGreen(), color.getBlue()))
                : ((byte) this.palette.getIndex(color));*/
        this.data.setPixel(x, y, ColorCache.rgbToMap(color.getRed(), color.getGreen(), color.getBlue()));
    }

    public MapGraphics<?, ?> getData() {
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

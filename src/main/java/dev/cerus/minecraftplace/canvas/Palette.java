package dev.cerus.minecraftplace.canvas;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a color palette. Maps indexes to colors and vice versa.
 */
public class Palette {

    private final Map<Integer, Color> indexToColorMap = new HashMap<>();
    private final Map<Color, Integer> colorToIndexMap = new HashMap<>();

    /**
     * Register a <index, color> pair.
     *
     * @param index The color index
     * @param color The color
     */
    public void set(final int index, final Color color) {
        this.indexToColorMap.put(index, color);
        this.colorToIndexMap.put(color, index);
    }

    /**
     * Get a color by its index.
     *
     * @param index The color index
     *
     * @return The color
     */
    public Color getColor(final int index) {
        return this.indexToColorMap.get(index);
    }

    /**
     * Get an index by its color.
     *
     * @param color The color
     *
     * @return The color index
     */
    public int getIndex(final Color color) {
        if (!this.colorToIndexMap.containsKey(color)) {
            throw new IllegalArgumentException("Color is not part of this palette");
        }
        return this.colorToIndexMap.get(color);
    }

    public Map<Integer, Color> getColors() {
        return Map.copyOf(this.indexToColorMap);
    }

}

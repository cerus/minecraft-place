package dev.cerus.minecraftplace.world;

import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import java.util.HashSet;
import java.util.Set;

public class WorldContext {

    private final int totalWidth;
    private final int totalHeight;
    private final int startX;
    private final int startY;
    private final int startZ;
    private final Set<Long> loadedChunks;

    public WorldContext(final MinecraftPlacePlugin plugin, final int startX, final int startY, final int startZ) {
        this(
                plugin.getCanvasMap().values().stream()
                        .mapToInt(value -> value.getX() + value.getWidth())
                        .max()
                        .orElse(0),
                plugin.getCanvasMap().values().stream()
                        .mapToInt(value -> value.getY() + value.getHeight())
                        .max()
                        .orElse(0),
                startX,
                startY,
                startZ
        );
    }

    public WorldContext(final int totalWidth, final int totalHeight, final int startX, final int startY, final int startZ) {
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.loadedChunks = new HashSet<>();

        System.out.println("CONTEXT " + totalWidth + ", " + totalHeight);
    }

    public boolean isInBounds(final int x, final int z) {
        return x >= this.startX && x < this.startX + this.totalWidth
                && z >= this.startZ && z < this.startZ + this.totalHeight;
    }

    public int getTotalWidth() {
        return this.totalWidth;
    }

    public int getTotalHeight() {
        return this.totalHeight;
    }

    public int getStartX() {
        return this.startX;
    }

    public int getStartY() {
        return this.startY;
    }

    public int getStartZ() {
        return this.startZ;
    }

    public void markChunkLoaded(final int x, final int z) {
        this.loadedChunks.add(this.chunkKey(x, z));
    }

    public void markChunkUnloaded(final int x, final int z) {
        this.loadedChunks.remove(this.chunkKey(x, z));
    }

    public boolean isChunkLoaded(final int x, final int z) {
        return this.loadedChunks.contains(this.chunkKey(x, z));
    }

    private long chunkKey(final int x, final int z) {
        return x | ((long) z << 32L);
    }

    public Set<Long> getLoadedChunks() {
        return this.loadedChunks;
    }

}

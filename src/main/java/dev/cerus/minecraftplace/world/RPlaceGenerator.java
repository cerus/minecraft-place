package dev.cerus.minecraftplace.world;

import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import dev.cerus.minecraftplace.reddit.canvas.Canvas;
import java.awt.Color;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class RPlaceGenerator extends ChunkGenerator {

    private final MinecraftPlacePlugin plugin;
    private final BlockColorCache blockColorCache;
    private final WorldContext worldContext;

    public RPlaceGenerator(final MinecraftPlacePlugin plugin, final BlockColorCache blockColorCache, final WorldContext worldContext) {
        this.plugin = plugin;
        this.blockColorCache = blockColorCache;
        this.worldContext = worldContext;
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull final World world, @NotNull final Random random, final int x, final int z, @NotNull final BiomeGrid biome) {
        final ChunkData chunkData = this.createChunkData(world);
        if (true) {
            for (int xx = 0; xx < 16; xx++) {
                for (int yy = 0; yy < 16; yy++) {
                    chunkData.setBlock(xx, 1, yy, Material.BARRIER);
                }
            }
            return chunkData;
        }

        final int blockX = x * 16;
        final int blockZ = z * 16;

        //Bukkit.broadcastMessage("Generating " + x + " " + z);

        Canvas theCanvas = null;
        for (final Canvas canvas : this.plugin.getCanvasMap().values()) {
            //Bukkit.broadcastMessage("Canvas check " + canvas.getX() + " " + canvas.getY());
            if (blockX - this.worldContext.getStartX() >= canvas.getX()
                    && blockX - this.worldContext.getStartX() < canvas.getX() + canvas.getWidth()
                    && blockZ - this.worldContext.getStartZ() >= canvas.getY()
                    && blockZ - this.worldContext.getStartZ() < canvas.getY() + canvas.getHeight()) {
                theCanvas = canvas;
                break;
            }
        }
        if (theCanvas == null) {
            return chunkData;
        }

        //Bukkit.broadcastMessage("Generating " + x + " " + z + " canvas " + theCanvas.getX() + " " + theCanvas.getY());

        int blocks = 0;
        for (int xx = 0; xx < 16; xx++) {
            if (xx + blockX < this.worldContext.getStartX() + theCanvas.getX()
                    || xx + blockX >= this.worldContext.getStartX() + theCanvas.getX() + theCanvas.getWidth()) {
                continue;
            }

            for (int yy = 0; yy < 16; yy++) {
                if (yy + blockZ < this.worldContext.getStartZ() + theCanvas.getY()
                        || yy + blockZ >= this.worldContext.getStartZ() + theCanvas.getY() + theCanvas.getHeight()) {
                    continue;
                }

                blocks++;
                final Color pixel = theCanvas.getPixel(blockX + xx - theCanvas.getX(), blockZ + yy - theCanvas.getY());
                chunkData.setBlock(blockX + xx, this.worldContext.getStartY(), blockZ + yy, this.blockColorCache.getMaterial(pixel));
            }
        }
        //Bukkit.broadcastMessage("Generating " + x + " " + z + " canvas " + theCanvas.getX() + " " + theCanvas.getY() + " DONE blocks " + blocks);

        return chunkData;
    }

}

package dev.cerus.minecraftplace.world;

import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import dev.cerus.minecraftplace.reddit.canvas.Canvas;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import java.awt.Color;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    private final WorldContext worldContext;
    private final World placeWorld;
    private final MinecraftPlacePlugin plugin;
    private final BlockColorCache blockColorCache;
    private final PlayerChunkController playerChunkController;

    public ChunkListener(final WorldContext worldContext,
                         final World placeWorld,
                         final MinecraftPlacePlugin plugin,
                         final BlockColorCache blockColorCache,
                         final PlayerChunkController playerChunkController) {
        this.worldContext = worldContext;
        this.placeWorld = placeWorld;
        this.plugin = plugin;
        this.blockColorCache = blockColorCache;
        this.playerChunkController = playerChunkController;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnsend(final PlayerChunkUnloadEvent event) {
        if (!event.getWorld().getName().equals(this.placeWorld.getName())) {
            return;
        }
        this.playerChunkController.remove(event.getPlayer(), event.getChunk());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkSend(final PlayerChunkLoadEvent event) {
        if (!event.getWorld().getName().equals(this.placeWorld.getName())) {
            return;
        }

        final Chunk chunk = event.getChunk();
        final int startX = chunk.getX() * 16;
        final int startZ = chunk.getZ() * 16;

        Canvas theCanvas = null;
        for (final Canvas canvas : this.plugin.getCanvasMap().values()) {
            //Bukkit.broadcastMessage("Canvas check " + canvas.getX() + " " + canvas.getY());
            if (startX >= canvas.getX() && startX < canvas.getX() + canvas.getWidth()
                    && startZ >= canvas.getY() && startZ < canvas.getY() + canvas.getHeight()) {
                theCanvas = canvas;
                break;
            }
        }
        if (theCanvas == null) {
            return;
        }

        this.playerChunkController.add(event.getPlayer(), chunk);

        int blocks = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final int canvasX = startX + x;
                final int canvasZ = startZ + z;
                if (canvasX < theCanvas.getX() || canvasZ < theCanvas.getY()
                        || canvasX >= theCanvas.getX() + theCanvas.getWidth()
                        || canvasZ >= theCanvas.getY() + theCanvas.getHeight()) {
                    continue;
                }
                event.getPlayer().sendBlockChange(new Location(
                        chunk.getWorld(),
                        canvasX,
                        0,
                        canvasZ
                ), this.blockColorCache.getMaterial(theCanvas.getPixel(canvasX - theCanvas.getX(), canvasZ - theCanvas.getY())).createBlockData());
                blocks++;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnload(final ChunkUnloadEvent event) {
        if (true) {
            return;
        }
        if (!event.getWorld().getName().equals(this.placeWorld.getName())) {
            return;
        }
        event.setSaveChunk(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkLoad(final ChunkLoadEvent event) {
        if (true) {
            return;
        }
        if (event.isNewChunk()) {
            return;
        }
        if (!event.getWorld().getName().equals(this.placeWorld.getName())) {
            return;
        }

        final Chunk chunk = event.getChunk();
        if (!this.worldContext.isInBounds(chunk.getX() * 16, chunk.getZ() * 16)) {
            return;
        }

        //Bukkit.broadcastMessage("RPlace chunk " + chunk.getX() + " " + chunk.getZ());

        final int blockX = chunk.getX() * 16;
        final int blockZ = chunk.getZ() * 16;

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
            return;
        }

        //Bukkit.broadcastMessage("Generating " + x + " " + z + " canvas " + theCanvas.getX() + " " + theCanvas.getY());

        chunk.addPluginChunkTicket(this.plugin);
        final Canvas finalTheCanvas = theCanvas;
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            int blocks = 0;
            for (int xx = 0; xx < 16; xx++) {
                if (xx + blockX < this.worldContext.getStartX() + finalTheCanvas.getX()
                        || xx + blockX >= this.worldContext.getStartX() + finalTheCanvas.getX() + finalTheCanvas.getWidth()) {
                    continue;
                }

                for (int yy = 0; yy < 16; yy++) {
                    if (yy + blockZ < this.worldContext.getStartZ() + finalTheCanvas.getY()
                            || yy + blockZ >= this.worldContext.getStartZ() + finalTheCanvas.getY() + finalTheCanvas.getHeight()) {
                        continue;
                    }

                    blocks++;
                    final Color pixel = finalTheCanvas.getPixel(blockX + xx - finalTheCanvas.getX() - this.worldContext.getStartX(), blockZ + yy - finalTheCanvas.getY() - this.worldContext.getStartZ());
                    chunk.getBlock(xx, this.worldContext.getStartY(), yy).setType(this.blockColorCache.getMaterial(pixel), false);
                }
            }
            //chunk.removePluginChunkTicket(this.plugin);
        }, 3);
    }

}

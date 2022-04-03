package dev.cerus.minecraftplace.world;

import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import dev.cerus.minecraftplace.reddit.canvas.Canvas;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BlockUpdateTask implements Runnable {

    private final Map<Integer, byte[]> previousCanvasData = new HashMap<>();
    private final MinecraftPlacePlugin plugin;
    private final PlayerChunkController playerChunkController;
    private final BlockColorCache blockColorCache;

    public BlockUpdateTask(final MinecraftPlacePlugin plugin, final PlayerChunkController playerChunkController, final BlockColorCache blockColorCache) {
        this.plugin = plugin;
        this.playerChunkController = playerChunkController;
        this.blockColorCache = blockColorCache;
    }

    @Override
    public void run() {
        for (final int canvasId : this.plugin.getCanvasMap().keySet()) {
            final Canvas canvas = this.plugin.getCanvasMap().get(canvasId);
            final byte[] previousData = this.previousCanvasData.computeIfAbsent(canvasId, o -> canvas.getData());
            final byte[] currentData = canvas.getData();

            int changes = 0;
            int changesb = 0;
            for (int x = 0; x < canvas.getWidth(); x++) {
                for (int y = 0; y < canvas.getHeight(); y++) {
                    final byte previous = previousData[x + y * canvas.getWidth()];
                    final byte current = currentData[x + y * canvas.getWidth()];
                    if (current != previous) {
                        final Material mat = this.blockColorCache.getMaterial(canvas.getPalette().getColor(current));
                        final long chunkKey = Chunk.getChunkKey(x >> 4, y >> 4);

                        final int finalX = x;
                        final int finalY = y;
                        final Collection<Player> viewers = this.playerChunkController.all(chunkKey);
                        viewers.forEach(player -> player.sendBlockChange(new Location(
                                player.getWorld(),
                                finalX,
                                0,
                                finalY
                        ), mat.createBlockData()));
                        changes += viewers.size();
                        changesb += 1;
                    }
                }
            }
            this.previousCanvasData.put(canvasId, Arrays.copyOf(currentData, currentData.length));
        }
    }

}

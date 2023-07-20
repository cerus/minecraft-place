package dev.cerus.minecraftplace.map;

import dev.cerus.maps.api.MapScreen;
import dev.cerus.maps.api.graphics.MapGraphics;
import dev.cerus.maps.plugin.map.MapScreenRegistry;
import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import dev.cerus.minecraftplace.reddit.canvas.Canvas;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Puts the canvas data onto MapScreens. Will update all screens that have a size of 16x8.
 */
public class MapUpdateTask implements Runnable {

    private static final double MAX_DIST = Math.pow(48, 2);

    private final Map<Integer, Set<UUID>> screenViewerMap = new HashMap<>();
    private final MinecraftPlacePlugin plugin;
    private int offsetX;
    private int offsetY;

    public MapUpdateTask(final MinecraftPlacePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Get screens to update
        final List<MapScreen> screens = MapScreenRegistry.getScreens().stream()
                .filter(screen -> screen.getWidth() == this.plugin.getScreenWidth())
                .filter(screen -> screen.getHeight() == this.plugin.getScreenHeight())
                .toList();
        if (screens.isEmpty()) {
            return;
        }

        final Collection<Canvas> canvases = this.plugin.getCanvasMap().values();

        if (this.offsetX == 0) {
            final int width = this.plugin.getScreenWidth() * 128;
            final int canvasWidth = canvases.stream()
                    .mapToInt(c -> c.getX() + c.getWidth())
                    .max().orElse(0);
            this.offsetX = (width - canvasWidth) / 2;
        }
        if (this.offsetY == 0) {
            final int height = this.plugin.getScreenHeight() * 128;
            final int canvasHeight = canvases.stream()
                    .mapToInt(c -> c.getY() + c.getHeight())
                    .max().orElse(0);
            this.offsetY = (height - canvasHeight) / 2;
        }

        // Update screens
        for (final MapScreen screen : screens) {
            for (final Canvas canvas : canvases) {
                final MapGraphics<?, ?> graphics = screen.getGraphics();
                graphics.place(canvas.getData(), this.offsetY + canvas.getX(), this.offsetY + canvas.getY());
            }

            final Collection<Player> receivers = new HashSet<>();
            for (final Player player : Bukkit.getOnlinePlayers()) {
                final Set<UUID> viewers = this.screenViewerMap.computeIfAbsent(screen.getId(), $ -> new HashSet<>());
                final double dist = player.getLocation().distanceSquared(screen.getLocation());
                if (viewers.contains(player.getUniqueId()) && dist > MAX_DIST) {
                    // Remove
                    viewers.remove(player.getUniqueId());
                    screen.destroyFrames(player);
                } else if (!viewers.contains(player.getUniqueId()) && dist < MAX_DIST) {
                    // Add
                    viewers.add(player.getUniqueId());
                    screen.spawnFrames(player);
                    screen.sendMaps(true, player);
                }
                if (viewers.contains(player.getUniqueId())) {
                    // Update
                    receivers.add(player);
                }
            }
            if (!receivers.isEmpty()) {
                screen.sendMaps(false, receivers);
            }
        }

        // Remove offline players
        for (final Integer key : this.screenViewerMap.keySet()) {
            final Set<UUID> uuids = this.screenViewerMap.get(key);
            for (final UUID uuid : Set.copyOf(uuids)) {
                if (Bukkit.getPlayer(uuid) == null) {
                    uuids.remove(uuid);
                }
            }
        }
    }

}

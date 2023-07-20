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

    private static final double MAX_DIST = Math.pow(32, 2);

    private final Map<Integer, Set<UUID>> screenViewerMap = new HashMap<>();
    private final MinecraftPlacePlugin plugin;

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

        // Update screens
        for (final MapScreen screen : screens) {
            for (final Canvas canvas : canvases) {
                final MapGraphics<?, ?> graphics = screen.getGraphics();
                graphics.place(canvas.getData(), canvas.getX(), canvas.getY());
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
    }

}

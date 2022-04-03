package dev.cerus.minecraftplace.task;

import dev.cerus.maps.api.MapScreen;
import dev.cerus.maps.api.graphics.ColorCache;
import dev.cerus.maps.plugin.map.MapScreenRegistry;
import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import java.awt.Color;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Puts the canvas data onto MapScreens. Will update all screens that have a size of 16x8.
 */
public class MapUpdateTask implements Runnable {

    private final MinecraftPlacePlugin plugin;

    public MapUpdateTask(final MinecraftPlacePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // Get screens to update
        final List<MapScreen> screens = MapScreenRegistry.getScreens().stream()
                .filter(screen -> screen.getWidth() == 16)
                .filter(screen -> screen.getHeight() == 8)
                .toList();
        if (screens.isEmpty()) {
            return;
        }

        // Update screens
        for (final MapScreen mapScreen : screens) {
            this.plugin.getCanvasMap().forEach((integer, canvas) -> {
                final int baseX = canvas.getX() + 24;
                final int baseY = canvas.getY() + 12;

                for (int x = 0; x < canvas.getWidth(); x++) {
                    for (int y = 0; y < canvas.getHeight(); y++) {
                        final Color pixel = canvas.getPixel(x, y);
                        final byte mapColor = ColorCache.rgbToMap(pixel.getRed(), pixel.getGreen(), pixel.getBlue());
                        mapScreen.getGraphics().setPixel(baseX + x, baseY + y, mapColor);
                    }
                }
            });
            mapScreen.sendMaps(false);
            mapScreen.sendFrames(Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }
    }

}

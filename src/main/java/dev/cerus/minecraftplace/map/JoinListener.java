package dev.cerus.minecraftplace.map;

import dev.cerus.maps.plugin.map.MapScreenRegistry;
import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Responsible for sending canvas data to players when they join
 */
public class JoinListener implements Listener {

    private final MinecraftPlacePlugin plugin;

    public JoinListener(final MinecraftPlacePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        MapScreenRegistry.getScreens().stream()
                .filter(screen -> screen.getWidth() == this.plugin.getScreenWidth())
                .filter(screen -> screen.getHeight() == this.plugin.getScreenHeight())
                .forEach(screen -> screen.sendMaps(true, player));
    }

}

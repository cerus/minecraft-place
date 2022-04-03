package dev.cerus.minecraftplace.listener;

import dev.cerus.maps.plugin.map.MapScreenRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Responsible for sending canvas data to players when they join
 */
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        MapScreenRegistry.getScreens().stream()
                .filter(screen -> screen.getWidth() == 16)
                .filter(screen -> screen.getHeight() == 8)
                .forEach(screen -> screen.sendMaps(true, player));
    }

}

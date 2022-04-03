package dev.cerus.minecraftplace.world;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandAlias("rplace")
public class RPlaceCommand extends BaseCommand {

    @Dependency
    private MinecraftPlacePlugin plugin;

    @Subcommand("tp")
    public void handleTp(final Player player) {
        final World world = Bukkit.getWorld(this.plugin.getConfig().getString("world.name"));
        player.teleport(world.getSpawnLocation());
    }

    @Subcommand("tphome")
    public void handleTpHome(final Player player) {
        final World world = Bukkit.getWorlds().iterator().next();
        player.teleport(world.getSpawnLocation());
    }

}

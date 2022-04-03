package dev.cerus.minecraftplace.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class PlayerChunkController {

    private final Map<UUID, Set<Long>> playerChunkMap = new HashMap<>();

    public Collection<Player> all(final long key) {
        return this.playerChunkMap.keySet().stream()
                .filter(uuid -> this.playerChunkMap.get(uuid).contains(key))
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }

    public void add(final Player player, final Chunk chunk) {
        final Set<Long> chunkIds = this.playerChunkMap
                .computeIfAbsent(player.getUniqueId(), o -> new HashSet<>());
        chunkIds.add(chunk.getChunkKey());
    }

    public void remove(final Player player, final Chunk chunk) {
        final Set<Long> chunkIds = this.playerChunkMap
                .computeIfAbsent(player.getUniqueId(), o -> new HashSet<>());
        chunkIds.remove(chunk.getChunkKey());
    }

    public void clear(final Player player) {
        this.playerChunkMap.remove(player.getUniqueId());
    }

}

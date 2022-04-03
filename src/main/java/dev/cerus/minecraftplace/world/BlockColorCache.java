package dev.cerus.minecraftplace.world;

import dev.cerus.maps.api.graphics.ColorCache;
import dev.cerus.minecraftplace.reddit.canvas.Palette;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_16_R3.MaterialMapColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;

public class BlockColorCache {

    private final Map<Integer, Material> colorToMaterialMap = new HashMap<>();
    private final Palette palette;

    public BlockColorCache(final Palette palette) {
        this.palette = palette;
    }

    public Material getMaterial(final Color color) {
        final int index = this.palette.getIndex(color);
        return this.colorToMaterialMap.computeIfAbsent(index, o -> this.findMaterial(color));
    }

    private Material findMaterial(final Color color) {
        final byte mapColor = ColorCache.rgbToMap(color.getRed(), color.getGreen(), color.getBlue());
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .filter(material -> {
                    final BlockData blockData = material.createBlockData();
                    final MaterialMapColor nmsMapColor = ((CraftBlockData) blockData).getState().d(null, null);
                    return ((byte) (nmsMapColor.aj * 4)) / 4 == mapColor / 4;
                })
                .findFirst()
                .orElse(Material.BEDROCK);
    }

}

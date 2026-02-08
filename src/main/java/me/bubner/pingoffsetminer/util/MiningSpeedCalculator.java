package me.bubner.pingoffsetminer.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;

/**
 * Utility to calculate ticks to mine from a specific SB block.
 */
public class MiningSpeedCalculator {
    public final static HashMap<String, Integer> blockHardnesses = new HashMap<>() {{
        // https://hypixel-skyblock.fandom.com/wiki/Block_Strength (2025-04-24)
        // For all intents and purposes, we only care about the blocks most commonly mined and have relevant strength

        // Dwarven ores, we just use the vanilla mapping since they are fundamentally the same
        String[] ores = {
                "minecraft:coal_block",
                "minecraft:iron_block",
                "minecraft:gold_block",
                "minecraft:lapis_block",
                "minecraft:redstone_block",
                "minecraft:emerald_block",
                "minecraft:diamond_block",
                "minecraft:quartz_block",
        };
        for (String ore : ores)
            put(ore, 600);
        // Dwarven metals
        put("skyblock:gray_mithril", 500);
        put("skyblock:green_mithril", 800);
        put("skyblock:blue_mithril", 1500);
        put("skyblock:titanium", 2000);
        // Gemstones
        put("skyblock:ruby_gemstone", 2300);
        put("skyblock:amber_gemstone", 3000);
        put("skyblock:sapphire_gemstone", 3000);
        put("skyblock:jade_gemstone", 3000);
        put("skyblock:amethyst_gemstone", 3000);
        put("skyblock:opal_gemstone", 3000);
        put("skyblock:topaz_gemstone", 3800);
        put("skyblock:jasper_gemstone", 4800);
        put("skyblock:onyx_gemstone", 5200);
        put("skyblock:aquamarine_gemstone", 5200);
        put("skyblock:citrine_gemstone", 5200);
        put("skyblock:peridot_gemstone", 5200);
        put("skyblock:tungsten", 5600);
        put("skyblock:umber", 5600);
        put("skyblock:glacite", 6000);
    }};

    public static String getBlockName(Block block) {
        try {
            if (block == Blocks.GRAY_WOOL || block == Blocks.CYAN_TERRACOTTA)
                return "skyblock:gray_mithril";
            if (block == Blocks.PRISMARINE
                    || block == Blocks.PRISMARINE_BRICKS
                    || block == Blocks.DARK_PRISMARINE)
                return "skyblock:green_mithril";
            if (block == Blocks.LIGHT_BLUE_WOOL)
                return "skyblock:blue_mithril";
            if (block == Blocks.POLISHED_DIORITE)
                return "skyblock:titanium";
            if (block == Blocks.RED_STAINED_GLASS || block == Blocks.RED_STAINED_GLASS_PANE)
                return "skyblock:ruby_gemstone";
            if (block == Blocks.ORANGE_STAINED_GLASS || block == Blocks.ORANGE_STAINED_GLASS_PANE)
                return "skyblock:amber_gemstone";
            if (block == Blocks.LIGHT_BLUE_STAINED_GLASS || block == Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)
                return "skyblock:sapphire_gemstone";
            if (block == Blocks.LIME_STAINED_GLASS || block == Blocks.LIME_STAINED_GLASS_PANE)
                return "skyblock:jade_gemstone";
            if (block == Blocks.PURPLE_STAINED_GLASS || block == Blocks.PURPLE_STAINED_GLASS_PANE)
                return "skyblock:amethyst_gemstone";
            if (block == Blocks.WHITE_STAINED_GLASS || block == Blocks.WHITE_STAINED_GLASS_PANE)
                return "skyblock:opal_gemstone";
            if (block == Blocks.YELLOW_STAINED_GLASS || block == Blocks.YELLOW_STAINED_GLASS_PANE)
                return "skyblock:topaz_gemstone";
            if (block == Blocks.MAGENTA_STAINED_GLASS || block == Blocks.MAGENTA_STAINED_GLASS_PANE)
                return "skyblock:jasper_gemstone";
            if (block == Blocks.BLACK_STAINED_GLASS || block == Blocks.BLACK_STAINED_GLASS_PANE)
                return "skyblock:onyx_gemstone";
            if (block == Blocks.BLUE_STAINED_GLASS || block == Blocks.BLUE_STAINED_GLASS_PANE)
                return "skyblock:aquamarine_gemstone";
            if (block == Blocks.BROWN_STAINED_GLASS || block == Blocks.BROWN_STAINED_GLASS_PANE)
                return "skyblock:citrine_gemstone";
            if (block == Blocks.GREEN_STAINED_GLASS || block == Blocks.GREEN_STAINED_GLASS_PANE)
                return "skyblock:peridot_gemstone";
            if (block == Blocks.CLAY || block == Blocks.INFESTED_STONE)
                return "skyblock:tungsten";
            if (block == Blocks.BROWN_TERRACOTTA
                    || block == Blocks.TERRACOTTA
                    || block == Blocks.RED_SANDSTONE_SLAB
                    || block == Blocks.RED_SANDSTONE)
                return "skyblock:umber";
            if (block == Blocks.PACKED_ICE)
                return "skyblock:glacite";
        } catch (IllegalArgumentException e) {
            // Swallowed
        }

        return BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName();
    }

    public static double getTicksToBreak(int blockHardness, double miningSpeed) {
        if (blockHardness == -1 || miningSpeed == -1)
            return -1;
        return Math.round(blockHardness * 30 / miningSpeed);
    }
}

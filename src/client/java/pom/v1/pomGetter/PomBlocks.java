package pom.v1.pomGetter;

import java.util.HashMap;

public class PomBlocks {
    public static HashMap<String, Boolean> getBlocks() {
        HashMap<String, Boolean> blocks = new HashMap<>();

        blocks.put("minecraft:obsidian", true);

        String[] ores = {
                "minecraft:coal_block",
                "minecraft:iron_block",
                "minecraft:gold_block",
                "minecraft:lapis_block",
                "minecraft:redstone_block",
                "minecraft:emerald_block",
                "minecraft:diamond_block",
                "minecraft:quartz_block"
        };
        for (String ore : ores) {
            blocks.put(ore, true);
        }

// Dwarven metals
        blocks.put("skyblock:gray_mithril", true);
        blocks.put("skyblock:green_mithril", true);
        blocks.put("skyblock:blue_mithril", true);
        blocks.put("skyblock:titanium", true);
        blocks.put("skyblock:tungsten", true);
        blocks.put("skyblock:umber", true);
        blocks.put("skyblock:glacite", true);

// Gemstones
        blocks.put("skyblock:ruby_gemstone", true);
        blocks.put("skyblock:amber_gemstone", true);
        blocks.put("skyblock:sapphire_gemstone", true);
        blocks.put("skyblock:jade_gemstone", true);
        blocks.put("skyblock:amethyst_gemstone", true);
        blocks.put("skyblock:opal_gemstone", true);
        blocks.put("skyblock:topaz_gemstone", true);
        blocks.put("skyblock:jasper_gemstone", true);
        blocks.put("skyblock:onyx_gemstone", true);
        blocks.put("skyblock:aquamarine_gemstone", true);
        blocks.put("skyblock:citrine_gemstone", true);
        blocks.put("skyblock:peridot_gemstone", true);

        return blocks;
    }
}

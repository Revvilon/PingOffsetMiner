package pom.v1;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class SpeedCalc {
    public final static HashMap<String, Integer> blockHardness = new HashMap<String, Integer>() {{

        // https://hypixel-skyblock.fandom.com/wiki/Block_Strength


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

    }};

    public static String getBlockName(Block block, BlockPos eventPos) {
        try {
            if (block == Blocks.ORANGE_STAINED_GLASS || block == Blocks.ORANGE_STAINED_GLASS_PANE)
                return "skyblock:amber_gemstone";
            if (block == Blocks.YELLOW_STAINED_GLASS || block == Blocks.YELLOW_STAINED_GLASS_PANE)
                return "skyblock:topaz_gemstone";
            if (block == Blocks.LIGHT_BLUE_STAINED_GLASS || block == Blocks.LIGHT_BLUE_STAINED_GLASS_PANE)
                return "skyblock:sapphire_gemstone";
            if (block == Blocks.RED_STAINED_GLASS || block == Blocks.RED_STAINED_GLASS_PANE)
                return "skyblock:ruby_gemstone";
            if (block == Blocks.LIME_STAINED_GLASS || block == Blocks.LIME_STAINED_GLASS_PANE)
                return "skyblock:jade_gemstone";
            if (block == Blocks.WHITE_STAINED_GLASS || block == Blocks.WHITE_STAINED_GLASS_PANE)
                return "skyblock:opal_gemstone";
            if (block == Blocks.PURPLE_STAINED_GLASS || block == Blocks.PURPLE_STAINED_GLASS_PANE)
                return "skyblock:amethyst_gemstone";
            if (block == Blocks.PINK_STAINED_GLASS || block == Blocks.PINK_STAINED_GLASS_PANE)
                return "skyblock:jasper_gemstone";
            if (block == Blocks.BLACK_STAINED_GLASS || block == Blocks.BLACK_STAINED_GLASS_PANE)
                return "skyblock:onyx_gemstone";
            if (block == Blocks.BLUE_STAINED_GLASS || block == Blocks.BLUE_STAINED_GLASS_PANE)
                return "skyblock:onyx_gemstone";
            if (block == Blocks.BROWN_STAINED_GLASS || block == Blocks.BROWN_STAINED_GLASS_PANE)
                return "skyblock:citrine_gemstone";
            if (block == Blocks.GREEN_STAINED_GLASS || block == Blocks.GREEN_STAINED_GLASS_PANE)
                return "skyblock:peridot_gemstone";


        } catch (IllegalArgumentException e) {
            return "whar";
        }
        return "whar????";
    }

    public static double getTicksToBreak(int blockHardness, double miningSpeed) {
        if (blockHardness == -1 || miningSpeed == -1)
            return -1;

        return Math.round(blockHardness * 30 / miningSpeed);
    }
}

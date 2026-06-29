package pom.rewrite.features.toggles;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingHash;
import pom.rewrite.utility.block.BlockUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockToggles {
    public static final Feature instance = new Feature("enabled_blocks");

    public static final SettingHash blocks = new SettingHash(getDefaults(), "blocks", instance);

    private static Map<String, Boolean> defaultBlocks;

    private static Map<String, Boolean> getDefaults() {
        if (defaultBlocks != null) return defaultBlocks;

        defaultBlocks = new LinkedHashMap<>();
        BlockUtil.getBlocks().forEach((key, value) -> {
            value.block.forEach(block -> {
                if (block.contains("pane")) return;
                defaultBlocks.put(block, true);
            });
        });

        return defaultBlocks;
    }

}

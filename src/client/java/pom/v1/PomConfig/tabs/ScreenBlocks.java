package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;
import pom.v1.pomGetter.SpeedCalc;

import java.util.ArrayList;
import java.util.Collection;

import static pom.v1.PomConfig.PomConfig.Config;

public class ScreenBlocks {

    public ConfigCategory getCategory() {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Blocks"))

                .groups(blocks())

                .build();
    }

    private Collection<OptionGroup> blocks() {

        Collection<OptionGroup> groups = new ArrayList<>();

        var metCat = OptionGroup.createBuilder().name(Component.literal("Metals"));
        var gemCat = OptionGroup.createBuilder().name(Component.literal("Gemstones"));
        var oreCat = OptionGroup.createBuilder().name(Component.literal("Ores"));

        SpeedCalc.blockHardness.forEach((key, value) -> {
            Config().blockEnabled.putIfAbsent(key, true);
            OptionGroup.Builder let = null;
            if (key.contains("gem")) let = gemCat;
            if (key.contains("skyblock") && !key.contains("gem")) let = metCat;
            if (key.contains("minecraft")) let = oreCat;
            if (let == null) return;
            let.option(Option.<Boolean>createBuilder()
                    .name(Component.literal(key.replaceAll("^.*:", "").replace("_", " ")))
                    .binding(
                            true,
                            () -> Config().blockEnabled.get(key),
                            newVal -> Config().blockEnabled.put(key, newVal)
                    )
                    .controller(TickBoxControllerBuilder::create)
                    .build());
        });

        groups.add(metCat.build());
        groups.add(gemCat.build());
        groups.add(oreCat.build());

        return groups;
    }
}

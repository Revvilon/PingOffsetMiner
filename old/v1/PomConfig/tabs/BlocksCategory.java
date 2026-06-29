package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

import static pom.v1.PomConfig.PomConfig.Config;

public class BlocksCategory implements TabBuilder {

    private final String tabID = "Blocks";

    public ConfigCategory buildTab() {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder().name(Component.literal(tabID));
        addOptions(categoryBuilder);
        return categoryBuilder.build();
    }

    private void addOptions(ConfigCategory.Builder categoryBuilder) {
        var metCat = OptionGroup.createBuilder().name(Component.literal("Metals"));
        var gemCat = OptionGroup.createBuilder().name(Component.literal("Gemstones"));
        var oreCat = OptionGroup.createBuilder().name(Component.literal("Ores"));

        Config().blockEnabled.forEach((key, value) -> {
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

        categoryBuilder.group(metCat.build());
        categoryBuilder.group(gemCat.build());
        categoryBuilder.group(oreCat.build());
    }
}

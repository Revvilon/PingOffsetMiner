package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;
import pom.v1.pomGetter.PomIslandData;

import static pom.v1.PomConfig.PomConfig.Config;

public class ScreenIslands {

    public ConfigCategory getCategory() {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Islands"))

                .group(islands())

                .build();
    }

    private OptionGroup islands() {
        var group = OptionGroup.createBuilder();

        PomIslandData.getIslands().forEach((key, value) -> {
            Config().islandEnabled.putIfAbsent(key, value);

            group.option(Option.<Boolean>createBuilder()
                            .name(Component.literal(key))
                            .binding(
                                    value,
                                    () -> Config().islandEnabled.get(key),
                                    newVal -> Config().islandEnabled.put(key, newVal)
                            )
                            .controller(TickBoxControllerBuilder::create)
                    .build());
        });

        return group.build();
    }
}

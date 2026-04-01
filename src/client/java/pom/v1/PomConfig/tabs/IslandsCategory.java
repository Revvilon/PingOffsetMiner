package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;
import pom.v1.PomConfig.OptionBuilder;
import pom.v1.pomGetter.PomIslandData;

import static pom.v1.PomConfig.PomConfig.Config;

public class IslandsCategory implements TabBuilder {
    private final String tabID = "Islands";

    public ConfigCategory buildTab() {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder().name(Component.literal(tabID));
        addOptions(categoryBuilder);
        return categoryBuilder.build();
    }

    private void addOptions(ConfigCategory.Builder categoryBuilder) {
        Config().islandEnabled.forEach((entry, key) -> {
                categoryBuilder.option(Option.<Boolean>createBuilder()
                                .name(Component.literal(entry))
                                .binding(
                                        key,
                                        () -> Config().islandEnabled.get(entry),
                                        val -> Config().islandEnabled.put(entry, val)
                                )
                                .controller(TickBoxControllerBuilder::create)
                        .build());
        });
    }
}

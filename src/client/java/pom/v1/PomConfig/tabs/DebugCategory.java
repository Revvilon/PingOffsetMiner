package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

import static pom.v1.PomConfig.OptionBuilder.build;
import static pom.v1.PomConfig.OptionBuilder.buildLinked;
import static pom.v1.PomConfig.PomConfig.Config;

public class DebugCategory implements TabBuilder {
    private final String tabID = "Debug";

    public ConfigCategory buildTab() {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder().name(Component.literal(tabID));
        addOptions(categoryBuilder);
        return categoryBuilder.build();
    }

    private void addOptions(ConfigCategory.Builder categoryBuilder) {
        categoryBuilder.group(buildLinked(OptionGroup.createBuilder()
                .option(build("Debugging enabled", false, Config().debug, TickBoxControllerBuilder::create).build())
                .option(build("Custom Mining Speed", 0.0, Config().speed, opt -> DoubleSliderControllerBuilder.create(opt).step(100.0).range(0.0, 50000.0)).build())
                .option(build("Custom Ping", 0.0, Config().ping, opt -> DoubleSliderControllerBuilder.create(opt).range(0.0, 500.0).step(10.0)).build())
                .option(build("Custom TPS", 0.0, Config().tps, opt -> DoubleSliderControllerBuilder.create(opt).step(1.0).range(0.0, 20.0)).build())
                .build()));

        categoryBuilder.option(build("Logging", false, Config().shouldLog, TickBoxControllerBuilder::create).build());
    }
}

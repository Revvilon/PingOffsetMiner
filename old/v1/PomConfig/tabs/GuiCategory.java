package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static pom.v1.PomConfig.OptionBuilder.build;
import static pom.v1.PomConfig.OptionBuilder.buildLinked;
import static pom.v1.PomConfig.PomConfig.Config;

public class GuiCategory implements TabBuilder {

    private final String tabID = "Gui - BETA";

    public ConfigCategory buildTab() {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder().name(Component.literal(tabID));
        addOptions(categoryBuilder);
        return categoryBuilder.build();
    }

    public void addOptions(ConfigCategory.Builder categoryBuilder) {
        categoryBuilder.group(buildLinked(OptionGroup.createBuilder()
                .option(build("Tick display enabled", true, Config().tickDisplay.active, TickBoxControllerBuilder::create).build())
                .option(build("Block not broken color", Color.red, Config().tickDisplay.c1, opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                .option(build("Block broken color", Color.green, Config().tickDisplay.c2, opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                .option(build("Horizontal position", 50, Config().tickDisplay.x, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
                .option(build("Vertical position", 48, Config().tickDisplay.y, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
                .option(build("Scale", 100, Config().tickDisplay.size, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
                .build())
                ).build();

        var group = new ArrayList<Option<?>>(List.of(
                        build("Efficiency display enabled", true, Config().efficiencyDisplay.active, TickBoxControllerBuilder::create).build(),
                        build("Timeout value", 30, Config().efficiencyDisplaySec, opt -> IntegerSliderControllerBuilder.create(opt).range(1, 100).step(1)).build(),
                        build("Horizontal position", 5, Config().efficiencyDisplay.x, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build(),
                        build("Vertical position", 50, Config().efficiencyDisplay.y, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build(),
                        build("Scale", 100, Config().efficiencyDisplay.size, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build()
        ));
        buildLinked(group);

        group.add(LabelOption.create(Component.literal("")));
        group.add(build("Extra information - DEBUG ONLY", false, Config().debugGui, TickBoxControllerBuilder::create).build());

        categoryBuilder.group(OptionGroup.createBuilder()
                        .options(group)
                .build());

        categoryBuilder.group(buildLinked(OptionGroup.createBuilder()
                .option(build("Performance HUD enabled", false, Config().performanceDisplay.active, TickBoxControllerBuilder::create).build())
                .option(build("Scale", 80, Config().performanceDisplay.size, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
                .build()));
    }
}

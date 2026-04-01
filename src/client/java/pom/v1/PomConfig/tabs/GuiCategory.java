package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import pom.v1.Util;

import java.lang.reflect.Array;
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
                .option(build("Tick display enabled", Config().tickDisplay.active, TickBoxControllerBuilder::create).build())
                .option(build("Block not broken color", Config().tickDisplay.c1, opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                .option(build("Block broken color", Config().tickDisplay.c2, opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                .option(build("Horizontal position", Config().tickDisplay.x, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
                .option(build("Vertical position", Config().tickDisplay.y, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
               // .option(build("Scale", Config().tickDisplay.size, opt -> FloatSliderControllerBuilder.create(opt).step(.1F).range(0F, 3F)).build())
                .build())
                ).build();
        categoryBuilder.group(buildLinked(OptionGroup.createBuilder()
                .option(build("Efficiency display enabled", Config().efficiencyDisplay.active, TickBoxControllerBuilder::create).build())
                .option(build("Timeout value", Config().efficiencyDisplaySec, opt -> IntegerSliderControllerBuilder.create(opt).range(1, 100).step(1)).build())
                .option(build("Horizontal position", Config().efficiencyDisplay.x, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
                .option(build("Vertical position", Config().efficiencyDisplay.y, opt -> IntegerSliderControllerBuilder.create(opt).step(1).range(0, 100)).build())
               // .option(build("Scale", Config().efficiencyDisplay.size, opt -> FloatSliderControllerBuilder.create(opt).step(.1F).range(0F, 3F)).build())
        .build()));
        categoryBuilder.option(build("Extra information in gui (for debugging", Config().debugGui, TickBoxControllerBuilder::create).build());
    }
}

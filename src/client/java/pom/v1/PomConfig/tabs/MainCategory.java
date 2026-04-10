package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import pom.v1.PomConfig.PomConfig;
import pom.v1.PomConfig.dataHolder.renderSettings;

import java.awt.*;
import java.util.List;

import static pom.v1.PingOffsetMinerClient.MOD_ID;
import static pom.v1.PomConfig.OptionBuilder.build;
import static pom.v1.PomConfig.OptionBuilder.buildLinked;
import static pom.v1.PomConfig.PomConfig.Config;

public class MainCategory implements TabBuilder {
    private final String tabID = "Ping Offset Miner";


    public ConfigCategory buildTab() {
        ConfigCategory.Builder categoryBuilder = ConfigCategory.createBuilder().name(Component.literal(tabID));
        addOptions(categoryBuilder);
        return categoryBuilder.build();
    }

    private void addOptions(ConfigCategory.Builder categoryBuilder) {

        categoryBuilder.option(build("Enabled", true, Config().active, TickBoxControllerBuilder::create).build());

        categoryBuilder.group(groupBuilder("Line", Config().line));
        categoryBuilder.group(groupBuilder("Highlight", Config().highlight));

        categoryBuilder.group(OptionGroup.createBuilder().name(Component.literal("Sound"))
                        .options(buildLinked(List.of(
                                build("Sound enabled", false, Config().sound, TickBoxControllerBuilder::create).build(),
                                build("Sound path:", "", Config().soundpath, StringControllerBuilder::create).build(),
                                ButtonOption.createBuilder()
                                                .name(Component.literal("List of sounds"))
                                                .text(Component.literal("[CLICK]").withStyle(ChatFormatting.BOLD))
                                                .action(((YACLScreen, thisOption) -> Util.getPlatform().openUri("https://www.digminecraft.com/lists/sound_list_pc.php")))                        .build())
                                )).build()

                        )
                .build();

        categoryBuilder.group(OptionGroup.createBuilder()
                .name(Component.literal("Experimental"))
                        .options(buildLinked(List.of(
                                build("Toggle when using MSB", false, Config().ability,  TickBoxControllerBuilder::create).build(),
                                build("When using MSB:", PomConfig.msbToggle.OFF, Config().msbToggleValue, opt -> EnumControllerBuilder.create(opt).enumClass(PomConfig.msbToggle.class)).build()
                        )))
                        .option(LabelOption.create(Component.literal("")))
                        .options(buildLinked(List.of(
                                build("Extra mining speed on Gemstones", true, Config().extra, TickBoxControllerBuilder::create).build(),
                                build("Amount of extra speed", 855.0, Config().extraVal, DoubleFieldControllerBuilder::create).build()
                        )))
                .build());
    }

    private OptionGroup groupBuilder(String name, renderSettings prop) {

        Color c1 = name.contains("Line") ? Color.red : new Color(255, 0, 0, 50);
        Color c2 = name.contains("Line") ? Color.green : new Color(0, 255, 0, 50);

        return buildLinked(OptionGroup.createBuilder()
                .option(build(name + " active", true, prop.active, TickBoxControllerBuilder::create).build())

                .option(build(name + " block not broken color", c1, prop.c1, opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                .option(build(name + " block broken color", c2, prop.c2, opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())

                .option(build(name + " depth test", false, prop.depth, BooleanControllerBuilder::create)
                        .description(OptionDescription.createBuilder()
                                .image(Identifier.fromNamespaceAndPath(MOD_ID, "/img/" + name.toLowerCase() + ".png"), 64, 64)
                                .build())
                        .build())

                .optionIf(name.contains("Line"), build("Line thickness", 2.0, Config().line.width, opt -> DoubleSliderControllerBuilder.create(opt).range(0.0, 10.0).step(0.1)).build())
                .build());
    }
}

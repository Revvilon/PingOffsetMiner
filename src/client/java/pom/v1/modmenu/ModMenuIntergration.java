package pom.v1.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.awt.*;


public class ModMenuIntergration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        pomConfig Config = pomConfig.HANDLER.instance();
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Text.literal(""))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("POM - Ping-Offset-Miner"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Active"))
                                .description(OptionDescription.of(Text.literal("Toggles POM on / off")))
                                .controller(TickBoxControllerBuilder::create)
                                .binding(
                                        true,
                                        () -> Config.active,
                                        newVal -> Config.active = newVal
                                )
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Outline personalization:"))
                                .option(Option.<Color>createBuilder() /*Config.properties.setProperty("c1", ("#" + Integer.toHexString(newVal.getRed()) + Integer.toHexString(newVal.getBlue()) + Integer.toHexString(newVal.getGreen())))*/
                                        .name(Text.literal("Block not broken color:"))
                                        .description(OptionDescription.of(Text.literal("The color of the block outline that shows when a block is not yet broken")))
                                        .binding(
                                                new Color(255, 0, 0),
                                                () -> Config.color1,
                                                newVal -> Config.color1 = newVal
                                        )
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false)
                                        )
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Block broken color:"))
                                        .description(OptionDescription.of(Text.literal("The color of the block outline that shows when a block is broken")))
                                        .binding(
                                                new Color(0, 255, 0),
                                                () -> Config.color2,
                                                newVal -> Config.color2 = newVal
                                        )
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false)
                                        )
                                        .build())
                                .option(Option.<pomConfig.line>createBuilder()
                                        .name(Text.literal("Line style"))
                                        .binding(
                                                pomConfig.line.ThinLine,
                                                () -> Config.selectedLine,
                                                newVal -> Config.selectedLine = newVal
                                        )
                                        .controller(opt -> EnumControllerBuilder.create(opt)
                                                .enumClass(pomConfig.line.class)
                                        )
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Sounds"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Play a sound when block is broken"))
                                        .binding(
                                                false,
                                                () -> Config.sound,
                                                newVal -> Config.sound = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.literal("Path to sound:"))
                                        .binding(
                                                "",
                                                () -> Config.soundpath,
                                                newVal -> Config.soundpath = newVal
                                        )
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.literal("List of sounds"))
                                        .text(Text.literal("Open URL"))
                                        .action((YACLScreen, thisOption) -> {
                                            Util.getOperatingSystem().open("https://www.digminecraft.com/lists/sound_list_pc.php");
                                        })
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Debugging"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Custom stats enabled"))
                                .binding(
                                        false,
                                        () -> Config.debug,
                                        newVal -> Config.debug = newVal
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Text.literal("Custom mining speed"))
                                .binding(
                                        0.0,
                                        () -> Config.speed,
                                        newVal -> Config.speed = newVal
                                )
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0, 10000.0)
                                        .step(10.0))

                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Text.literal("Custom ping"))
                                .binding(
                                        0.0,
                                        () -> Config.ping,
                                        newVal -> Config.ping = newVal
                                )
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                        .range(0.0,300.0)
                                        .step(5.0))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Assume +855ms"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Text.literal("Lapidary + HOTM + Blue Cheese omelette"))
                                                .build())
                                        .binding(
                                                true,
                                                () -> Config.extra,
                                                newVal -> Config.extra = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .save(pomConfig::save)
                .build().generateScreen(parentScreen);
    }
}

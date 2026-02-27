package pom.v1.modmenu;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import pom.v1.SpeedCalc;

import java.awt.*;

public class pomGui {


    public static Screen createScreen(Screen parent) {
        var metCat = OptionGroup.createBuilder().name(Component.literal("Metals"));
        var gemCat = OptionGroup.createBuilder().name(Component.literal("Gemstones"));
        var oreCat = OptionGroup.createBuilder().name(Component.literal("Ores"));


        pomConfig Config = pomConfig.HANDLER.instance();

        SpeedCalc.blockHardness.forEach((key, value) -> {
            Config.blockEnabled.putIfAbsent(key, true);
            OptionGroup.Builder let = null;
            if (key.contains("gem")) let = gemCat;
            if (key.contains("skyblock") && !key.contains("gem")) let = metCat;
            if (key.contains("minecraft")) let = oreCat;
            if (let == null) return;
            let.option(Option.<Boolean>createBuilder()
                    .name(Component.literal(key.replaceAll("^.*:", "").replace("_", " ")))
                            .binding(
                                    true,
                                    () -> Config.blockEnabled.get(key),
                                    newVal -> Config.blockEnabled.put(key, newVal)
                            )
                            .controller(TickBoxControllerBuilder::create)
                            .build());
        });


        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal(""))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("POM - Ping-Offset-Miner"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Active"))
                                .description(OptionDescription.of(Component.literal("Toggles POM on / off")))
                                .controller(TickBoxControllerBuilder::create)
                                .binding(
                                        true,
                                        () -> Config.active,
                                        newVal -> Config.active = newVal
                                )
                                .build())
                       .group(OptionGroup.createBuilder()
                                .name(Component.literal("Outline"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Outline active"))
                                        .binding(
                                                true,
                                                () -> Config.lineactive,
                                                newVal -> Config.lineactive = newVal
                                        )
                                                .controller(TickBoxControllerBuilder::create)
                                        .build())
                                        .option(Option.<Color>createBuilder()
                                                .name(Component.literal("Block not broken line color:"))
                                                .description(OptionDescription.of(Component.literal("The color of the block outline that shows when a block is not yet broken")))
                                                .binding(
                                                        new Color(255, 0, 0, 255),
                                                        () -> Config.color1,
                                                        newVal -> Config.color1 = newVal
                                                )
                                                .controller(opt -> ColorControllerBuilder.create(opt)
                                                        .allowAlpha(true)
                                                )
                                                .build())
                                        .option(Option.<Color>createBuilder()
                                                .name(Component.literal("Block broken line color:"))
                                                .description(OptionDescription.of(Component.literal("The color of the block outline that shows when a block is broken")))
                                                .binding(
                                                        new Color(0, 255, 0, 255),
                                                        () -> Config.color2,
                                                        newVal -> Config.color2 = newVal
                                                )
                                                .controller(opt -> ColorControllerBuilder.create(opt)
                                                        .allowAlpha(true)
                                                )
                                                .build())
                                        .option(Option.<Double>createBuilder()
                                                .name(Component.literal("Line width"))
                                                .binding(
                                                        5.0,
                                                        () -> Config.lineWidth,
                                                        newVal -> Config.lineWidth = newVal
                                                )
                                                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                                        .range(0.0, 40.0)
                                                        .step(1.0))
                                                .build())
                        .build())

                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Block highlight"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Block highlight active"))
                                        .binding(
                                                true,
                                                () -> Config.blockactive,
                                                newVal -> Config.blockactive = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                                .name(Component.literal("Block not broken highlight color"))
                                                .binding(
                                                        new Color(255, 0, 0, 50),
                                                        () -> Config.blockCol1,
                                                        newVal -> Config.blockCol1 = newVal
                                                )
                                                .controller(opt -> ColorControllerBuilder.create(opt)
                                                        .allowAlpha(true)
                                                )
                                                .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.literal("Block broken highlight color"))
                                        .binding(
                                                new Color(0, 255, 0, 50),
                                                () -> Config.blockCol2,
                                                newVal -> Config.blockCol2 = newVal
                                        )
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(true)
                                        )
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Sounds"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Play a sound when block is broken"))
                                        .binding(
                                                false,
                                                () -> Config.sound,
                                                newVal -> Config.sound = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Component.literal("Path to sound:"))
                                        .binding(
                                                "",
                                                () -> Config.soundpath,
                                                newVal -> Config.soundpath = newVal
                                        )
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("List of sounds"))
                                        .text(Component.literal("Open URL"))
                                        .action((YACLScreen, thisOption) -> {
                                            Util.getPlatform().openUri("https://www.digminecraft.com/lists/sound_list_pc.php");
                                        })
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Enabled blocks"))
                        .group(gemCat.build())
                        .group(metCat.build())
                        .group(oreCat.build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Debugging"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Debugging enabled"))
                                .binding(
                                        false,
                                        () -> Config.debug,
                                        newVal -> Config.debug = newVal
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Custom stats"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.literal("Only use when debugging"))
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Component.literal("Custom mining speed"))
                                        .binding(
                                                0.0,
                                                () -> Config.speed,
                                                newVal -> Config.speed = newVal
                                        )
                                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                                .range(0.0, 20000.0)
                                                .step(10.0))

                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Component.literal("Custom ping"))
                                        .binding(
                                                0.0,
                                                () -> Config.ping,
                                                newVal -> Config.ping = newVal
                                        )
                                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                                .range(0.0, 300.0)
                                                .step(5.0))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enable logging"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Component.literal("Sends information in the chat, only useful for reporting bugs"))
                                                .build())
                                        .binding(
                                                false,
                                                () -> Config.logging,
                                                newVal -> Config.logging = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Experimental"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Mining Speed fetch fix"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Component.literal("Experimental feature, tries to only fetch mining speed when holding drill. If you experience issues, disable this feature"))
                                                .build())
                                        .binding(
                                                false,
                                                () -> Config.drillSpeed,
                                                newVal -> Config.drillSpeed = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Turn off when using MSB"))
                                        .description(OptionDescription.createBuilder()
                                                .text(Component.literal("Reads tab widget for pickaxe ability and turns off when mining speed boost is used"))
                                                .build())
                                        .binding(
                                                false,
                                                () -> Config.ability,
                                                newVal -> Config.ability = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Extra mining speed on gemstones"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.literal("Lapidary + HOTM + Blue Cheese omelette, only applies to gemstones"))
                                        .build())
                                .binding(
                                        true,
                                        () -> Config.extra,
                                        newVal -> Config.extra = newVal
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("Amount of extra speed"))
                                .binding(
                                        855.0,
                                        () -> Config.extraVal,
                                        newVal -> Config.extraVal = newVal
                                )
                                .controller(DoubleFieldControllerBuilder::create)
                                .build())
                        .build())
                .save(pomConfig::save)
                .build().generateScreen(parent);
    }
}

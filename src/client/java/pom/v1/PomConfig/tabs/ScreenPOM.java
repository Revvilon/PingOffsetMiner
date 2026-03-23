package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import pom.v1.PomConfig.OptionBuilder;
import pom.v1.PomConfig.PomConfig;
import pom.v1.PomConfig.renderSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static pom.v1.PomConfig.PomConfig.Config;

public class ScreenPOM extends OptionBuilder {

    public ConfigCategory getCategory() {

        return ConfigCategory.createBuilder()
                .name(Component.literal("Ping Offset Miner"))

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Ping Offset Miner enabled"))
                        .binding(
                                true,
                                () -> Config().active,
                                newVal -> Config().active = newVal
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .group(buildGroup("Line", () -> Config().line, val -> Config().line = val))
                .group(buildGroup("Highlight", () -> Config().highlight, val -> Config().highlight = val))

                .group(soundOptions())
                .group(experimental())

                .build();
    }

    private OptionGroup buildGroup(String name, Supplier<renderSettings> getter, Consumer<renderSettings> setter) {
        boolean isLine = name.contains("Line");

        Color defC1 = isLine ? Color.red : new Color(255, 0, 0, 50);
        Color defC2 = isLine ? Color.green : new Color(0, 255, 0, 50);

        renderSettings settings = getter.get();

        ArrayList<Option<?>> options = new ArrayList<>();

        options.add(
                Option.<Color>createBuilder()
                        .name(Component.literal("Block not broken " + name + " color"))
                        .binding(
                                defC1,
                                () -> settings.c1,
                                newVal -> {
                                settings.c1 = newVal;
                                setter.accept(settings);
                                }
                        )
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .build()
        );

        options.add(
                Option.<Color>createBuilder()
                        .name(Component.literal("Block broken " + name + " color"))
                        .binding(
                                defC2,
                                () -> settings.c2,
                                newVal -> {
                                    settings.c2 =  newVal;
                                    setter.accept(settings);
                                }
                        )
                        .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                        .build()
        );

        if (isLine) {
            options.add(
                    Option.<Double>createBuilder()
                            .name(Component.literal(name + " width"))
                            .binding(
                                    5.0,
                                    () -> settings.width,
                                    newVal -> {
                                        settings.width = newVal;
                                        setter.accept(settings);
                                    }
                            )
                            .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                                    .step(1.0)
                                    .range(0.0, 100.0))
                            .build()
            );
        }

        Option<Boolean> master = Option.<Boolean>createBuilder()
                .name(Component.literal(name + " enabled"))
                .binding(
                        true,
                        () -> settings.active,
                        newVal -> {
                            settings.active = newVal;
                            setter.accept(settings);
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build();

        options.forEach(opt -> opt.setAvailable(settings.active));

        link(master, options);

        return OptionGroup.createBuilder()
                .name(Component.literal(name))
                .option(master)
                .options(options)
                .build();
    }

    OptionGroup soundOptions() {

        Option<?> option = Option.<String>createBuilder()
                .name(Component.literal("Sound path"))
                .binding(
                        "",
                        () -> Config().soundpath,
                        newVal -> Config().soundpath = newVal
                )
                .available(Config().sound)
                .controller(StringControllerBuilder::create)
                .build();

        Option<?> masterOption = Option.<Boolean>createBuilder()
                .name(Component.literal("Sound enabled"))
                .binding(
                        false,
                        () -> Config().sound,
                        newVal -> {
                            Config().sound = newVal;
                            option.setAvailable(newVal);
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build();

        return OptionGroup.createBuilder()
                .name(Component.literal("Sound"))
                .option(masterOption)
                .option(option)
                .option(ButtonOption.createBuilder()
                        .name(Component.literal("List of sounds"))
                        .text(Component.literal("Open URL"))
                        .action(((YACLScreen, thisOption) -> Util.getPlatform().openUri("https://www.digminecraft.com/lists/sound_list_pc.php")))

                        .build())
                .build();
    }

    OptionGroup experimental() {

        Option<?> toggleValue = Option.<PomConfig.msbToggle>createBuilder()
                .name(Component.literal("When using MSB:"))
                .binding(
                        PomConfig.msbToggle.ON,
                        () -> Config().msbToggleValue,
                        newVal -> Config().msbToggleValue = newVal
                )
                .available(Config().ability)
                .controller(opt -> EnumControllerBuilder.create(opt)
                        .enumClass(PomConfig.msbToggle.class)
                        .formatValue(val -> Component.literal(val.toString())))
                .build();

        Option<?> toggle = Option.<Boolean>createBuilder()
                .name(Component.literal("Toggle POM when using Mining Speed Boost"))
                .binding(
                        false,
                        () -> Config().ability,
                        newVal -> {
                            Config().ability = newVal;
                            toggleValue.setAvailable(newVal);
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> extraVal = Option.<Double>createBuilder()
                .name(Component.literal("Amount of extra speed"))
                .binding(
                        855.0,
                        () -> Config().extraVal,
                        newVal -> Config().extraVal = newVal
                )
                .available(Config().extra)
                .controller(DoubleFieldControllerBuilder::create)
                .build();

        Option<?> extraToggle = Option.<Boolean>createBuilder()
                .name(Component.literal("Extra mining speed when mining gemstones"))
                .binding(
                        true,
                        () -> Config().extra,
                        newVal -> {
                            Config().extra = newVal;
                            extraVal.setAvailable(newVal);
                        }
                )
                .controller(TickBoxControllerBuilder::create)
                .build();

        return OptionGroup.createBuilder()
                .name(Component.literal("Experimental"))
                .option(toggle)
                .option(toggleValue)
                .option(extraToggle)
                .option(extraVal)
                .build();
    }
}

package pom.v1.PomConfig.tabs;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;
import pom.v1.PomConfig.OptionBuilder;

import java.util.ArrayList;

import static pom.v1.PomConfig.PomConfig.Config;


public class ScreenDebug extends OptionBuilder {

    public ConfigCategory getCategory() {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Debug"))

                .group(statsGroup())
                .group(logGroup())

                .build();
    }

    private OptionGroup statsGroup() {

        ArrayList<Option<?>> options = new ArrayList<>();

        options.add(Option.<Double>createBuilder()
                .name(Component.literal("Custom mining speed"))
                .binding(
                        0.0,
                        () -> Config().speed,
                        newVal -> Config().speed = newVal
                )
                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                        .range(0.0, 20000.0)
                        .step(10.0))

                .build());

        options.add(Option.<Double>createBuilder()
                .name(Component.literal("Custom ping"))
                .binding(
                        0.0,
                        () -> Config().ping,
                        newVal -> Config().ping = newVal
                )
                .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                        .range(0.0, 300.0)
                        .step(5.0))
                .build());

        Option<Boolean> masterOption = Option.<Boolean>createBuilder()
                .name(Component.literal("Custom stats enabled"))
                .binding(
                        false,
                        () -> Config().debug,
                        newVal -> Config().debug = newVal
                )
                .controller(TickBoxControllerBuilder::create)
                .build();

        options.forEach(opt -> opt.setAvailable(Config().debug));

        link(masterOption, options);

        return OptionGroup.createBuilder()
                .name(Component.literal("Custom stats"))

                .option(masterOption)
                .options(options)

                .build();
    }

    private OptionGroup logGroup() {
        return OptionGroup.createBuilder()
                .name(Component.literal("Logging"))
                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Enable logging"))
                        .binding(
                                false,
                                () -> Config().logging,
                                newVal -> Config().logging = newVal
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .build();
    }
}

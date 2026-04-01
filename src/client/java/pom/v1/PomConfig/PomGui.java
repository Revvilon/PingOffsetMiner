package pom.v1.PomConfig;

import com.mojang.blaze3d.platform.DepthTestFunction;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.references.Blocks;
import pom.v1.PingOffsetMiner;
import pom.v1.PingOffsetMinerClient;
import pom.v1.PomConfig.tabs.*;
import pom.v1.render.PomRendering;

import static pom.v1.PomConfig.PomConfig.Config;

public class PomGui {

    public static Screen createScreen(Screen parent) {

        return YetAnotherConfigLib.createBuilder()
                .title(Component.empty())

                .category(new MainCategory().buildTab())
                .category(new GuiCategory().buildTab())
                .category(new IslandsCategory().buildTab())
                .category(new BlocksCategory().buildTab())
                .category(new DebugCategory().buildTab())
                .save(() -> {
                    Config().save();
                })
                .build()
                .generateScreen(parent);

    }
}
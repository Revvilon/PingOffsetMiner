package pom.v1.PomConfig;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pom.v1.PomConfig.tabs.*;

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
                .save(() -> Config().save())
                .build()
                .generateScreen(parent);

    }
}
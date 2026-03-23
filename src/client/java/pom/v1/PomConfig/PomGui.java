package pom.v1.PomConfig;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pom.v1.PomConfig.tabs.ScreenBlocks;
import pom.v1.PomConfig.tabs.ScreenDebug;
import pom.v1.PomConfig.tabs.ScreenIslands;
import pom.v1.PomConfig.tabs.ScreenPOM;

import static pom.v1.PomConfig.PomConfig.Config;

public class PomGui {

    public static Screen createScreen(Screen parent) {

        return YetAnotherConfigLib.createBuilder()
                .title(Component.empty())

                .category(new ScreenPOM().getCategory())
                .category(new ScreenIslands().getCategory())
                .category(new ScreenBlocks().getCategory())
                .category(new ScreenDebug().getCategory())

                .save(Config()::save)
                .build()
                .generateScreen(parent);

    }
}
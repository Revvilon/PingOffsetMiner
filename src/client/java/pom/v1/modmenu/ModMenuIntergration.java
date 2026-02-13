package pom.v1.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.tick.Tick;

import java.awt.*;
import java.net.URL;


public class ModMenuIntergration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
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
                                        () -> pomConfig.HANDLER.instance().active,
                                        newVal -> pomConfig.HANDLER.instance().active = newVal
                                )
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Outline personalization:"))
                                .option(Option.<Color>createBuilder() /*Config.properties.setProperty("c1", ("#" + Integer.toHexString(newVal.getRed()) + Integer.toHexString(newVal.getBlue()) + Integer.toHexString(newVal.getGreen())))*/
                                        .name(Text.literal("Block not broken color:"))
                                        .description(OptionDescription.of(Text.literal("The color of the block outline that shows when a block is not yet broken")))
                                        .binding(
                                                new Color(255, 0, 0),
                                                () -> pomConfig.HANDLER.instance().color1,
                                                newVal -> pomConfig.HANDLER.instance().color1 = newVal
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
                                                () -> pomConfig.HANDLER.instance().color2,
                                                newVal -> pomConfig.HANDLER.instance().color2 = newVal
                                        )
                                        .controller(opt -> ColorControllerBuilder.create(opt)
                                                .allowAlpha(false)
                                        )
                                        .build())
                                .option(Option.<pomConfig.line>createBuilder()
                                        .name(Text.literal("Line style"))
                                        .binding(
                                                pomConfig.line.ThinLine,
                                                () -> pomConfig.HANDLER.instance().selectedLine,
                                                newVal -> pomConfig.HANDLER.instance().selectedLine = newVal
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
                                                () -> pomConfig.HANDLER.instance().sound,
                                                newVal -> pomConfig.HANDLER.instance().sound = newVal
                                        )
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.literal("Path to sound:"))
                                        .binding(
                                                "pat",
                                                () -> pomConfig.HANDLER.instance().soundpath,
                                                newVal -> pomConfig.HANDLER.instance().soundpath = newVal
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
                .save(pomConfig::save)
                .build().generateScreen(parentScreen);
    }
}

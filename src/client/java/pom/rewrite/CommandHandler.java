package pom.rewrite;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import pom.rewrite.features.debug.Logging;
import pom.rewrite.features.render.OutlineRender;
import pom.rewrite.screen.click.ClickGui;
import pom.rewrite.screen.hud.HudEditScreen;
import pom.rewrite.utility.Util;
import pom.rewrite.utility.render.CustomPipelines;
import pom.rewrite.utility.render.RenderUtil;
import pom.rewrite.utility.server.ServerStats;

import java.util.Optional;

public class CommandHandler {

    private final Minecraft mc = Minecraft.getInstance();

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("pom")
                .executes(context -> {
                    mc.execute(() -> {
                        mc.setScreenAndShow(new ClickGui());
                    });
                    return -1;
                })
                .then(ClientCommands.literal("tps")
                        .executes(context -> {
                            Util.sendMsg(
                                    Component.empty()
                                            .append(Component.literal("TPS: ")).withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE)
                                            .append(Component.literal(String.format(String.format("%.1f", ServerStats.getTps()))).withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA))
                            );
                            return -1;
                        }))
                .then(ClientCommands.literal("ping")
                        .executes(context -> {
                            Util.sendMsg(
                                    Component.empty()
                                            .append(Component.literal("Ping: ")).withStyle(ChatFormatting.BOLD, ChatFormatting.WHITE)
                                            .append(Component.literal(String.valueOf(ServerStats.getPing())).withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA))
                            );
                            return -1;
                        }))
                .then(ClientCommands.literal("hud")
                        .executes(context -> {
                            mc.execute(() -> {
                                mc.setScreenAndShow(new HudEditScreen());
                            });
                            return -1;
                        }))
        );
        dispatcher.register(ClientCommands.literal("igetit").executes(context -> {
            mc.execute(() -> {
                Logging.igetit.setEnabled(true);
            });
            return -1;
        }));
        dispatcher.register(ClientCommands.literal("idontgetit").executes(context -> {
            mc.execute(() -> {
                Logging.igetit.setEnabled(false);
            });
            return -1;
        }));
    }
}

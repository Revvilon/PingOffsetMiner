package pom.v1.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import pom.v1.PingOffsetMinerClient;
import pom.v1.PomConfig.PomConfig;
import pom.v1.PomConfig.PomGui;
import pom.v1.Util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class pomCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pom")
                .executes(conComponent -> {
                    Minecraft client = Minecraft.getInstance();
                    
                    if (client.player == null) return 1;
                    client.execute(() -> client.setScreen(PomGui.createScreen(null)));
                    Util.sendMsg(Component.literal("Opened config!")
                            .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    );
                    return 1;
                })
                .then(literal("restart")
                        .executes(ctx -> {
                            Util.sendMsg(Component.literal("POM config restarted!"));
                            return 1;
                        }))
                .then(literal("ping")
                        .executes(ctx -> {
                            Util.sendMsg(Component.literal("Your ping is: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(String.valueOf(PingOffsetMinerClient.getPing()))
                                            .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                                    )
                            );
                            return 1;
                        }))
                .then(literal("tps")
                        .executes(ctx -> {
                            Util.sendMsg(Component.literal("Your tps is: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(String.valueOf((int) PingOffsetMinerClient.getTPS()))
                                            .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                                    )
                            );
                            return 1;
                        }))
                .then(literal("igetit")
                        .executes(ctx -> {
                            Util.sendMsg(Component.literal("Disabled warning message")
                                    .withStyle(ChatFormatting.WHITE));
                            PomConfig.Config().shouldWarn = false;
                            return 1;
                        })
                )
                .then(literal("idontgetit")
                        .executes(ctx -> {
                            Util.sendMsg(Component.literal("Enabled warning message")
                                    .withStyle(ChatFormatting.WHITE));
                            PomConfig.Config().shouldWarn = true;
                            return 1;
                        }))
        );
    }

}

package pom.v1.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import pom.v1.PingOffsetMinerClient;
import pom.v1.Util;
import pom.v1.modmenu.PomConfig;
import pom.v1.modmenu.PomGui;

import static java.lang.Math.round;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class pomCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pom")
                .executes(conComponent -> {
                    Minecraft client = Minecraft.getInstance();
                    
                    if (client.player == null) return 1;
                    client.execute(() -> {
                        client.setScreen(PomGui.createScreen(null));
                    });
                    Util.sendMsg(Component.literal("Opened config!")
                            .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    );
                    return 1;
                })
                .then(literal("restart")
                        .executes(ctx -> {
                            PomConfig.init();
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
        );
    }

}

package pom.v1.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import pom.v1.Util;
import pom.v1.modmenu.pomConfig;
import pom.v1.modmenu.pomGui;

import static java.lang.Math.round;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class pomCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pom")
                .executes(conComponent -> {
                    Minecraft client = Minecraft.getInstance();
                    
                    if (client.player == null) return 1;
                    client.execute(() -> {
                        client.setScreen(pomGui.createScreen(null));
                    });
                    Util.sendMsg(Component.literal("Opened config!")
                            .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD)
                    );
                    return 1;
                })
                .then(literal("restart")
                        .executes(ctx -> {
                            pomConfig.init();
                            Util.sendMsg(Component.literal("POM config restarted!"));
                            return 1;
                        }))
                .then(literal("ping")
                        .executes(ctx -> {
                            Util.sendMsg(Component.literal("Your ping is: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(String.valueOf(Util.getAverage(10)))
                                            .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                                    )
                            );
                            return 1;
                        }))
                .then(literal("tps")
                        .executes(ctx -> {
                            Util.sendMsg(Component.literal("Your tps is: ")
                                    .withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(String.valueOf(round(Util.tps)))
                                            .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
                                    )
                            );
                            return 1;
                        }))
        );
    }

}

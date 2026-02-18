package pom.v1.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pom.v1.Util;
import pom.v1.modmenu.pomConfig;
import pom.v1.modmenu.pomGui;

import static java.lang.Math.round;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class pomCommands {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pom")
                .executes(context -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.send(() -> client.setScreen(pomGui.createScreen(null)));
                    Util.sendMsg(Text.literal("Opened config!")
                            .formatted(Formatting.WHITE, Formatting.BOLD)
                    );
                    return 1;
                })
                .then(literal("restart")
                        .executes(ctx -> {
                            pomConfig.init();
                            Util.sendMsg(Text.literal("POM config restarted!"));
                            return 1;
                        }))
                .then(literal("ping")
                        .executes(ctx -> {
                            Util.sendMsg(Text.literal("Your ping is: ")
                                    .formatted(Formatting.GRAY)
                                    .append(Text.literal(String.valueOf(Util.getAverage(10)))
                                            .formatted(Formatting.AQUA, Formatting.BOLD)
                                    )
                            );
                            return 1;
                        }))
                .then(literal("tps")
                        .executes(ctx -> {
                            Util.sendMsg(Text.literal("Your tps is: ")
                                    .formatted(Formatting.GRAY)
                                    .append(Text.literal(String.valueOf(round(Util.tps)))
                                            .formatted(Formatting.AQUA, Formatting.BOLD)
                                    )
                            );
                            return 1;
                        }))
        );
    }

}

package pom.v1.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.util.Formatting;
import pom.v1.Config;
import pom.v1.Util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class pomSettings {
    public static void register(Config config, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pom")
                .executes(ctx -> {
                    Util.sendMsg("POM is: " + Config.getActive(), Formatting.GRAY);
                    return 1;
                })

                .then(argument("value", BoolArgumentType.bool())
                .executes(ctx -> {
                    boolean active = BoolArgumentType.getBool(ctx, "value");
                    Config.properties.setProperty("active", String.valueOf(active));
                    Util.sendMsg(active ? "Active!" : "Inactive!", Formatting.GRAY);
                    config.save();
                    return 1;
                })));

    }
}

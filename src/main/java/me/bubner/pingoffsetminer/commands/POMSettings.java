package me.bubner.pingoffsetminer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import me.bubner.pingoffsetminer.ModConfig;
import me.bubner.pingoffsetminer.util.Util;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

/**
 * /pom command
 */
public class POMSettings {
    private static final String USAGE = "/pom <speed | ping | active> <mining speed | ping(ms), true | false>";

    public static void register(ModConfig config, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pom")
                .then(literal("speed").then(argument("value", DoubleArgumentType.doubleArg(-1))
                        .executes(ctx -> {
                            double speed = DoubleArgumentType.getDouble(ctx, "value");
                            config.properties.setProperty("miningSpeed", String.valueOf(speed));
                            Util.sendMsg("mining speed set to " + speed);
                            config.save();
                            return 1;
                        })))
                .then(literal("active").then(argument("value", BoolArgumentType.bool())
                        .executes(ctx -> {
                            boolean active = BoolArgumentType.getBool(ctx, "value");
                            config.properties.setProperty("active", String.valueOf(active));
                            Util.sendMsg(active ? "active!" : "inactive!");
                            config.save();
                            return 1;
                        })))
                .then(literal("ping").then(argument("value", DoubleArgumentType.doubleArg(-1))
                        .executes(ctx -> {
                            double ping = DoubleArgumentType.getDouble(ctx, "value");
                            config.properties.setProperty("ping", String.valueOf(ping));
                            Util.sendMsg("ping (ms) set to " + ping);
                            config.save();
                            return 1;
                        })))
                .executes(ctx -> {
                    Util.sendMsg(USAGE);
                    double speed = config.getMiningSpeed();
                    boolean active = config.getActive();
                    double ping = config.getPing();
                    Util.sendMsg("mining speed: " + (speed == -1 ? "NOT SET" : speed) + ", ping: " + (ping == -1 ? "NOT SET" : ping) + ", active: " + active);
                    return 1;
                })
        );
    }
}

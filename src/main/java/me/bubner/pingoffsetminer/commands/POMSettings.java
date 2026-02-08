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
                .then(literal("speed")
                        .executes(ctx -> {
                            double speed = config.getMiningSpeed();
                            Util.sendMsg("Currently set mining speed: " + (speed == -1 ? "[not set]" : speed));
                            return 1;
                        })
                        .then(argument("value", DoubleArgumentType.doubleArg(-1))
                                .executes(ctx -> {
                                    double speed = DoubleArgumentType.getDouble(ctx, "value");
                                    config.properties.setProperty("miningSpeed", String.valueOf(speed));
                                    Util.sendMsg("Mining speed set to " + speed);
                                    config.save();
                                    return 1;
                                })))
                .then(literal("active")
                        .executes(ctx -> {
                            Util.sendMsg("Currently active: " + config.getActive());
                            return 1;
                        })
                        .then(argument("value", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean active = BoolArgumentType.getBool(ctx, "value");
                                    config.properties.setProperty("active", String.valueOf(active));
                                    Util.sendMsg(active ? "Active!" : "Inactive!");
                                    config.save();
                                    return 1;
                                })))
                .then(literal("ping")
                        .executes(ctx -> {
                            double ping = config.getPing();
                            Util.sendMsg("Currently set ping (ms): " + (ping == -1 ? "[not set]" : ping));
                            return 1;
                        })
                        .then(argument("value", DoubleArgumentType.doubleArg(-1))
                                .executes(ctx -> {
                                    double ping = DoubleArgumentType.getDouble(ctx, "value");
                                    config.properties.setProperty("ping", String.valueOf(ping));
                                    Util.sendMsg("Ping (ms) set to " + ping);
                                    config.save();
                                    return 1;
                                })))
                .executes(ctx -> {
                    Util.sendMsg(USAGE);
                    Util.sendMsg("Currently active: " + config.getActive());
                    double ping = config.getPing();
                    Util.sendMsg("Currently set ping (ms): " + (ping == -1 ? "[not set]" : ping));
                    double speed = config.getMiningSpeed();
                    Util.sendMsg("Currently set mining speed: " + (speed == -1 ? "[not set]" : speed));
                    return 1;
                })
        );
    }
}

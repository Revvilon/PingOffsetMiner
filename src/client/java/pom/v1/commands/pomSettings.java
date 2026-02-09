package pom.v1.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import pom.v1.Config;
import pom.v1.Util;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class pomSettings {
    private static final String USAGE = "/pom <speed (ms) | active (true/false)>";

    public static void register(Config config, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pom")
                .then(literal("speed")
                        .executes(ctx -> {
                            double speed = Config.getMiningSpeed();
                            Util.sendMsg("Current mining speed: " + (speed == -1 ? "[not set]" : speed));
                            return 1;
                        })
                        .then(argument("value", DoubleArgumentType.doubleArg(-1))
                                .executes(ctx -> {
                                    double speed = DoubleArgumentType.getDouble(ctx, "value");
                                    Config.properties.setProperty("miningSpeed", String.valueOf(speed));
                                    Util.sendMsg("Mining speed set to " + speed);
                                    config.save();
                                    return 1;
                                })))
                .then(literal("active")
                        .executes(ctx -> {
                            Util.sendMsg("Is active: " + Config.getActive());
                            return 1;
                        })
                .then(argument("value", BoolArgumentType.bool())
                .executes(ctx -> {
                    boolean active = BoolArgumentType.getBool(ctx, "value");
                    Config.properties.setProperty("active", String.valueOf(active));
                    Util.sendMsg(active ? "Active!" : "Inactive!");
                    config.save();
                    return 1;
                })))
                        .executes(ctx -> {
                            Util.sendMsg(USAGE);
                            Util.sendMsg("Active: " + Config.getActive());
                            double speed = Config.getMiningSpeed();
                            Util.sendMsg("Mining speed: " + (speed == -1 ? "[not set]" : speed));
                            return 1;
                        }));
    }
}

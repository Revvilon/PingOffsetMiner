package me.bubner.pingoffsetminer;

import me.bubner.pingoffsetminer.commands.POMSettings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ping Offset Miner. Port of the 1.8.9 Forge edition.
 *
 * @author Lucas Bubner, 2026
 */
public class PingOffsetMiner implements ClientModInitializer {
    public static final String MOD_ID = "pingoffsetminer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        final ModConfig MOD_CONFIG = new ModConfig();
        MOD_CONFIG.load();
        ClientCommandRegistrationCallback.EVENT.register(((commandDispatcher, registryAccess) ->
                POMSettings.register(MOD_CONFIG, commandDispatcher)));
        new BlockTimingOverlay(MOD_CONFIG);
    }
}
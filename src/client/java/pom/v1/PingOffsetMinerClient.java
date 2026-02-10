package pom.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.Ping;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import org.slf4j.LoggerFactory;
import pom.v1.commands.pomSettings;
import java.io.IOException;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {



		final Config MOD_CONFIG = new Config();
		try {
			MOD_CONFIG.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ClientCommandRegistrationCallback.EVENT.register((((commandDispatcher, commandRegistryAccess) -> {
			pomSettings.register(MOD_CONFIG, commandDispatcher);
		})));

	}

}

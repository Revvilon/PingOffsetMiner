package pom.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.mixin.client.rendering.InGameHudAccessor;
import net.fabricmc.fabric.mixin.networking.client.accessor.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.v1.commands.pomSettings;

import java.io.IOException;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static double getAveragePing(int sampleCount) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.inGameHud == null) return 0;

		DebugHud debugHud = (client.inGameHud.getDebugHud());
		MultiValueDebugSampleLogImpl pingLog = debugHud.getPingLog();

		if (pingLog == null) return 0;

		int availableEntries = Math.min(sampleCount, pingLog.getLength());
		if (availableEntries <= 0) return 0;

		long totalPing = 0;
		int count = 0;

		for (int i = 0; i < availableEntries; i++) {
			long sample = pingLog.get(i, 0);

			if (sample > 0) {
				totalPing += sample;
				count++;
			}
		}
		return count > 0 ? (double) totalPing / count : 0;
	}

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

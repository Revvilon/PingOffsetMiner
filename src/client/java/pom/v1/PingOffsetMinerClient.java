package pom.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.v1.commands.pomSettings;

import java.io.IOException;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final pingLatency latencyTracker = new pingLatency();


	@Override
	public void onInitializeClient() {

		ClientTickEvents.END_CLIENT_TICK.register(new ClientTickCallback());
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> latencyTracker.clear()));

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
	public static void sendQueryPing(MinecraftClient client) {
		if (client.getNetworkHandler() != null) {
			long startTime = System.currentTimeMillis();
			QueryPingC2SPacket packet = new QueryPingC2SPacket(startTime);
			client.getNetworkHandler().sendPacket(packet);
		}
	}

	public static long getAverageLatency() {
		Long latency = latencyTracker.getAverageLatency();
		return latency != null ? latency : 0L;
	}

	public static void onPingResultReceived(PingResultS2CPacket packet) {
		latencyTracker.recordPacketReceived(packet.startTime());
	}

	private static class ClientTickCallback implements ClientTickEvents.EndTick {
		private int tickCounter = 0;

		@Override
		public void onEndTick(MinecraftClient client) {
			tickCounter++;
			if (tickCounter >= 10)  {
				tickCounter = 0;
				sendQueryPing(client);
			}
		}
	}

}

package pom.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.v1.commands.pomSettings;

import java.io.IOException;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static int pping;
	int ticks = 20;
	int inter = 60 * ticks;
	final int[] counter = {inter};

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

        ClientTickEvents.END_WORLD_TICK.register(world -> {
			counter[0]++;

			if (counter[0] >= inter) {
				pingFunc();
				counter[0] = 0;
			}
		});
	}

	void pingFunc() {
		MinecraftClient client = MinecraftClient.getInstance();

		var handler = client.getNetworkHandler();

        assert handler != null;
        assert client.player != null;
        PlayerListEntry info = handler.getPlayerListEntry(client.player.getUuid());
		if (info != null) {
			pping = info.getLatency();
			PingOffsetMinerClient.LOGGER.info(String.valueOf(pping));
		}
	}
}
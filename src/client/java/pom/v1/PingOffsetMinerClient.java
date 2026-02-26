package pom.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.v1.commands.pomCommands;
import pom.v1.modmenu.pomConfig;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		pomConfig.init();

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
			pomCommands.register(commandDispatcher);
		});
	}
}
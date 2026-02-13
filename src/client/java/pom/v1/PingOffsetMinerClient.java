package pom.v1;

import it.unimi.dsi.fastutil.Hash;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.realms.Ping;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.v1.modmenu.pomConfig;

import javax.accessibility.AccessibleTable;
import java.io.IOException;
import java.util.HashMap;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static HashMap<String, Float> tickDeltas = new HashMap<>();
	public static HashMap<String, Float> tickTime = new HashMap<>();


	public static double getAverage(int sampleCount) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.inGameHud == null) return 0;

		MultiValueDebugSampleLogImpl log;

		DebugHud debugHud = (client.inGameHud.getDebugHud());
		log = debugHud.getPingLog();


		if (log == null) return 0;

		int availableEntries = Math.min(sampleCount, log.getLength());
		if (availableEntries <= 0) return 0;

		long total = 0;
		int count = 0;

		for (int i = 0; i < availableEntries; i++) {
			long sample = log.get(i, 0);

			if (sample > 0) {
				total += sample;
				count++;
			}
		}
		return count > 0 ? (double) total / count : 0;
	}

	public static double tps = 0;

	@Override
	public void onInitializeClient() {
		pomConfig.init();
    }
}

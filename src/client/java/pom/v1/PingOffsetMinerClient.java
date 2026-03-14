package pom.v1;

import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.v1.PomConfig.PomConfig;
import pom.v1.commands.pomCommands;
import pom.v1.pomGetter.PomBlockData;
import pom.v1.pomGetter.PomStats;
import pom.v1.pomGetter.SpeedCalc;
import pom.v1.pomNetwork.PomPing;
import pom.v1.pomNetwork.PomTPS;
import pom.v1.render.PomRendering;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static pom.v1.Util.log;
import static pom.v1.Util.shouldRender;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final IEventBus EVENT_BUS = new EventBus();
	public static PomStats POM_STATS = new PomStats();
	public static PomStats.MiningStats TOOL_STATS = POM_STATS.new MiningStats();
	public static PomPing POM_PING = new PomPing();
	public static PomTPS POM_TPS = new PomTPS();
	public static PomBlockData POM_BLOCK_DATA = new PomBlockData();
	public static PomBlockData.PomBlock POM_BLOCK = POM_BLOCK_DATA.new PomBlock();

	// Initialize variables
	BlockPos currentBlock;
	double ticksNeeded = -1;
	boolean timeoutExceeded = false;
	int startServerTick;
	boolean sound = false;

	@Override
	public void onInitializeClient() {
		PomConfig.init();


		EVENT_BUS.registerLambdaFactory("pom.v1", (lookupInMethod, klass) -> {
			try {
				return (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});

		EVENT_BUS.subscribe(POM_STATS);
		EVENT_BUS.subscribe(Util.class);
		EVENT_BUS.subscribe(POM_PING);
		EVENT_BUS.subscribe(POM_TPS);
		EVENT_BUS.subscribe(POM_BLOCK_DATA);
		PomRendering POM_RENDER = new PomRendering();

		PomConfig Config = PomConfig.HANDLER.instance();

		WorldRenderEvents.BEFORE_TRANSLUCENT.register(event -> {
			Minecraft client = Minecraft.getInstance();
			long time = System.currentTimeMillis();

			POM_BLOCK.setBlock(client);

			if (
					!Config.active ||
					!Util.getIsland() ||
					POM_BLOCK.isEmpty() ||
					client.player == null ||
					client.level == null ||
					!TOOL_STATS.isActive()
			)
			{
				log("Config is: " + Config.active, time);
				log("Block: " + POM_BLOCK.getName(), time);
				log("Tool is: " + TOOL_STATS.isActive(), time);
				log("Island is: " + Util.getIsland(), time);
				log("Debug is: " + Config.debug, time);
				timeoutExceeded = false;
				ticksNeeded = -1;
				startServerTick = -1;
				currentBlock = null;
				return;
			};

			VoxelShape blockShape = POM_BLOCK.getShape();
			BlockPos blockPos = POM_BLOCK.getBlockPos();

			if (!Objects.equals(currentBlock, blockPos) || !client.options.keyAttack.isDown()) {
				sound = false;
				currentBlock = blockPos;
				startServerTick = client.player.tickCount;
				log("Reset block breaking", time);
			}

				double debugSpeed = Config.debug ? Config.speed : TOOL_STATS.getSpeed();
				double extra = Config.extra ? Config.extraVal : 0;

				if (POM_BLOCK.getName().contains("gem")) {
					debugSpeed = debugSpeed + extra;
				}

				ticksNeeded = SpeedCalc.getTicksToBreak((int) POM_BLOCK.getHardness(), debugSpeed);


				if (sound && timeoutExceeded && Config.sound && client.options.keyAttack.isDown()) {
					sound = false;
					SoundEvent useSound = SoundEvent.createVariableRangeEvent(Identifier.parse(String.valueOf(SoundEvents.ALLAY_AMBIENT_WITH_ITEM) /*config soundpath*/));
					client.player.playSound(useSound);
				}
				if (!sound && !timeoutExceeded) sound = true;

				if (shouldRender()) {
					POM_RENDER.extractAndDraw(
							event,
							client,
							blockPos,
							blockShape,
							timeoutExceeded
					);
				}

				Util.log("Mining speed: " + debugSpeed, time);
				Util.log("Ticks needed: " + ticksNeeded, time);
				Util.log("Should render: " + shouldRender(), time);
		});

		ClientTickEvents.END_CLIENT_TICK.register(event -> {
			if (event.player == null || POM_BLOCK.isEmpty()) return;
			int ticksElapsed = event.player.tickCount - startServerTick;

			double debugTps = Config.debug ? 20.0 : getTPS();
			double pingSec = Config.debug ? Config.ping / 1000.0 : getPing() / 1000.0;

			double pingMath = debugTps * pingSec;

			double pingOffset = ticksNeeded - pingMath > pingMath
					? ticksNeeded - pingMath
					: ticksNeeded;
			timeoutExceeded = ticksNeeded > 0 && ticksElapsed >= pingOffset && event.options.keyAttack.isDown();
			Util.log("Ping offset: " +  pingOffset, System.currentTimeMillis());
		});

		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) -> {
			if (!POM_BLOCK.isEmpty()) {

				if (Config.debug && Config.lineactive /*config lineactive*/) return false;

				if (Config.active && Config.lineactive) return !shouldRender();

			}
			return true;
		});


		AttackBlockCallback.EVENT.register((player, level, hand, blockpos, hr) -> {

			if (!Util.foundSpeed() && !Config.debug && Config.active && Util.getIsland()) {
				Util.sendMsg(Component.literal("Mining Speed not found! Please enable in tab widget").withStyle(ChatFormatting.RED));
				Util.sendMsg(Component.literal("To enable: /tab -> Stats Widget -> Shown Stats -> Mining Speed").withStyle(ChatFormatting.RED));
				Util.sendMsg(Component.literal("Make sure that the mining speed stat is visible in your tab menu").withStyle(ChatFormatting.RED));

				return InteractionResult.FAIL;
			}

			return InteractionResult.PASS;
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> POM_PING.clear());

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> {
			pomCommands.register(commandDispatcher);
		});
	}

	public static double getTPS() {
		return POM_TPS.getAverageLatency();
	}
	public static long getPing() {
        return POM_PING.getAverageLatency();
	}
}
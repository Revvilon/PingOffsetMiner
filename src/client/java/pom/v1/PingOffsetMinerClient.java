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
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import pom.v1.PomConfig.PomConfig;
import pom.v1.commands.pomCommands;
import pom.v1.events.blockHitEvent;
import pom.v1.gui.PomScreen;
import pom.v1.pomGetter.PomBlockData;
import pom.v1.pomGetter.PomStats;
import pom.v1.pomNetwork.PomPing;
import pom.v1.pomNetwork.PomTPS;
import pom.v1.render.PomRendering;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static pom.v1.PomConfig.PomConfig.Config;
import static pom.v1.Util.*;

public class PingOffsetMinerClient implements ClientModInitializer {
	public static final String MOD_ID = "ping-offset-miner";
	public static final IEventBus EVENT_BUS = new EventBus();
	public static PomStats POM_STATS = new PomStats();
	public static PomStats.MiningStats TOOL_STATS = POM_STATS.new MiningStats();
	public static PomPing POM_PING = new PomPing();
	public static PomTPS POM_TPS = new PomTPS();
	public static PomBlockData POM_BLOCK_DATA = new PomBlockData();
	public static PomBlockData.PomBlock POM_BLOCK = new PomBlockData.PomBlock();
	public static PomCalc POM_CALC = new PomCalc();
	public static PomEfficiency POM_EFF = new PomEfficiency();

	// Initialize variables
	BlockPos currentBlock;
	boolean timeoutExceeded = false;
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
		EVENT_BUS.subscribe(PomScreen.class);
		EVENT_BUS.subscribe(POM_CALC);
		EVENT_BUS.subscribe(POM_EFF);
		PomRendering POM_RENDER = new PomRendering();


		PomScreen.initialize();

		WorldRenderEvents.END_MAIN.register(event -> {
			Minecraft client = Minecraft.getInstance();
			long time = System.currentTimeMillis();

			POM_BLOCK.setBlock(client);

			if (
					!Config().active.get() ||
					!Util.getIsland() ||
					POM_BLOCK.isEmpty() ||
					client.player == null ||
					client.level == null ||
					!TOOL_STATS.isActive()
			)
			{
				log("Config is: " + Config().active.get(), time);
				log("Block: " + POM_BLOCK.getName(), time);
				log("Tool is: " + TOOL_STATS.isActive(), time);
				log("Island is: " + Util.getIsland(), time);
				log("Debug is: " + Config().debug.get(), time);
				timeoutExceeded = false;
				currentBlock = null;
				POM_CALC.reset();
				return;
			}

			VoxelShape blockShape = POM_BLOCK.getShape();
			BlockPos blockPos = POM_BLOCK.getBlockPos();

			if (!Objects.equals(currentBlock, blockPos) || !client.options.keyAttack.isDown()) {
				sound = false;
				currentBlock = blockPos;
				POM_CALC.reset();
				log("Reset block breaking", time);
			}

			if (!sound && !POM_CALC.timeoutExceeded()) sound = true;
			if (sound && POM_CALC.timeoutExceeded() && Config().sound.get() && client.options.keyAttack.isDown()) {
				sound = false;
				Minecraft.getInstance().getSoundManager().play(
						SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(Identifier.withDefaultNamespace(Config().soundpath.get())), 1.0f)
				);
			}

				if (shouldRender()) {

					POM_RENDER.extractAndDraw(
							event,
							client,
							blockPos,
							blockShape,
							POM_CALC.timeoutExceeded()
					);
				}

				Util.log("Mining speed: " + POM_CALC.getSpeed(), time);
				Util.log("Ticks needed: " + POM_CALC.getTicksNeeded(), time);
				Util.log("Should render: " + shouldRender(), time);
		});

		ClientTickEvents.END_CLIENT_TICK.register(event -> {

		});

		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outline) -> {
			if (!POM_BLOCK.isEmpty() && getIsland()) {

				if (Config().debug.get() && Config().line.active.get()) return false;

				if (Config().active.get() && Config().line.active.get()) return !shouldRender();

			}
			return true;
		});


		MutableComponent warnText = Component.literal("[I GET IT]")
						.withStyle()
								.setStyle(Style.EMPTY
										.withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to disable this message")))
										.withClickEvent(new ClickEvent.RunCommand("/pom igetit")));

		AttackBlockCallback.EVENT.register((player, level, hand, blockpos, hr) -> {


			blockHitEvent event = new blockHitEvent(blockpos, POM_BLOCK.getName());
			EVENT_BUS.post(event);

			if (!Util.foundSpeed() && !Config().debug.get() && Config().active.get() && Util.getIsland() && Config().shouldWarn.get()) {
				Util.sendMsg(Component.literal("Mining Speed not found! Please enable in tab widget").withStyle(ChatFormatting.RED));
				Util.sendMsg(Component.literal("To enable: /tab -> Stats Widget -> Shown Stats -> Mining Speed").withStyle(ChatFormatting.RED));
				Util.sendMsg(Component.literal("Make sure that the mining speed stat is visible in your tab menu").withStyle(ChatFormatting.RED));
				Util.sendMsg(warnText.withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD, ChatFormatting.ITALIC));
			}

			return InteractionResult.PASS;
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> POM_PING.clear());

		ClientCommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess) -> pomCommands.register(commandDispatcher));
	}

	public static double getTPS() {
		if (Config().debug.get()) {return Config().tps.get();}
		return POM_TPS.getAverageLatency();
	}
	public static double getPing() {
		if (Config().debug.get()) {return Config().ping.get();}
        return POM_PING.getAverageLatency();
	}
}
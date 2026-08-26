package pom.rewrite;

import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pom.rewrite.config.ConfigHandler;
import pom.rewrite.config.Feature;
import pom.rewrite.events.clientTick;
import pom.rewrite.features.debug.Logging;
import pom.rewrite.screen.hud.HudManager;
import pom.rewrite.utility.Util;
import pom.rewrite.utility.block.BlockData;
import pom.rewrite.utility.block.BlockUtil;
import pom.rewrite.utility.render.RenderClass;
import pom.rewrite.utility.server.IslandUtils;
import pom.rewrite.utility.server.ServerStats;
import pom.rewrite.utility.sound.SoundUtil;
import pom.rewrite.utility.stats.EfficiencyStats;
import pom.rewrite.utility.stats.MiningStats;
import pom.rewrite.utility.stats.StatsReader;
import pom.rewrite.utility.stats.TickStats;

import java.lang.invoke.MethodHandles;

public class PingOffsetMinerClient implements ClientModInitializer {
    public static final String MOD_ID = "ping-offset-miner";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final IEventBus EVENT_BUS = new meteordevelopment.orbit.EventBus();

    @Override
    public void onInitializeClient() {

        System.setProperty("devauth.enabled", "true");

        ConfigHandler.load();


        EVENT_BUS.registerLambdaFactory("pom.rewrite", (lookupInMethod, klass) -> {
            try {
                return (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });

        BlockUtil.init();
        RenderClass.init();
        BlockData.getInstance().init();
        TickStats.instance().init();
        IslandUtils.init();
        HudManager.init();
        EfficiencyStats.getInstance().init();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, a) -> new CommandHandler().registerCommands(dispatcher));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientTick event = new clientTick();
            EVENT_BUS.post(event);
        });

        MutableComponent igetitToggle = Component.literal("[I GET IT]")
                .withStyle()
                .setStyle(Style.EMPTY
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to disable this message")))
                        .withClickEvent(new ClickEvent.RunCommand("/igetit")));


        Feature igetit = Logging.igetit;
        AttackBlockCallback.EVENT.register((mc, lvl, hand, pos, dir) -> {
            if (!igetit.isEnabled() && !StatsReader.getInstance().miningSpeedFound() && MiningStats.instance().shouldRender()) {
                Util.sendMsg(Component.literal("Mining Speed not found! Please enable in tab widget").withStyle(ChatFormatting.RED));
                Util.sendMsg(Component.literal("To enable: /tab -> Stats Widget -> Shown Stats -> Mining Speed").withStyle(ChatFormatting.RED));
                Util.sendMsg(Component.literal("Make sure that the mining speed stat is visible in your tab menu").withStyle(ChatFormatting.RED));
                Util.sendMsg(igetitToggle.withStyle(ChatFormatting.BOLD, ChatFormatting.AQUA));
            }

            return InteractionResult.PASS;
        });

        EVENT_BUS.subscribe(BlockData.getInstance());
        EVENT_BUS.subscribe(StatsReader.getInstance());
        EVENT_BUS.subscribe(TickStats.instance());
        EVENT_BUS.subscribe(EfficiencyStats.getInstance());
        EVENT_BUS.subscribe(MiningStats.instance());
        EVENT_BUS.subscribe(ServerStats.class);
        EVENT_BUS.subscribe(new SoundUtil());
    }
}

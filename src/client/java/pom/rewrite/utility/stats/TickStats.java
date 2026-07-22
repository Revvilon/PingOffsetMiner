package pom.rewrite.utility.stats;

import meteordevelopment.orbit.EventHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import pom.rewrite.events.clientTick;
import pom.rewrite.events.startBreak;
import pom.rewrite.features.debug.CustomStats;
import pom.rewrite.utility.Util;
import pom.rewrite.utility.block.BlockData;
import pom.rewrite.utility.block.BlockObject;

import java.util.Objects;



public class TickStats {
    private static final TickStats INSTANCE = new TickStats();
    private TickStats(){}
    public static TickStats instance(){return INSTANCE;}



    private int ticksNeeded = 0;
    private int ticksElapsed = 0;
    private double progressMade = 0.0;
    private BlockPos tempPos;
    private int startBreak = 0;

    private final MiningStats miningStats = MiningStats.instance();
    private final BlockData blockData = BlockData.getInstance();
    private final Minecraft mc = Minecraft.getInstance();

    public void init() {

        LevelRenderEvents.START_MAIN.register(world -> {
            if ((blockData.getCurrentBlock() instanceof BlockObject blockObject)) {
                if (!(mc.gameMode != null && mc.gameMode.isDestroying()) || !Objects.equals(blockObject.pos, tempPos)) {
                    tempPos = blockObject.pos;
                    reset();
                }
            }
        });
    }

    @EventHandler
    private void onStartBreak(startBreak event) {
        this.startBreak = event.time;
    }

    @EventHandler

    private void tickElapsed(clientTick event) {
        if (mc.level == null || mc.player == null || mc.gameMode == null) {
            forceReset();

            return;
        }


        if (blockData.getCurrentBlock() instanceof BlockObject blockObject) {
            if (mc.gameMode.isDestroying()) {
                int speed = miningStats.getMiningSpeed();
                String id = blockObject.id;

                if (id.contains("gem")) {
                    speed += CustomStats.extraSpeed.getInt();
                } else if (id.contains("skyblock")) {
                    speed += CustomStats.extraSpeedMetal.getInt();
                }

                progressMade += speed;
                ticksElapsed = mc.player.tickCount - startBreak;
            }

            setTicksNeeded(blockObject);
            return;
        }
        reset();
    }



    private void setTicksNeeded(BlockObject blockObject) {
        int speed = miningStats.getMiningSpeed();
        String id = blockObject.id;

        if (id.contains("gem")) {
            speed += CustomStats.extraSpeed.getInt();
        } else if (id.contains("skyblock")) {
            speed += CustomStats.extraSpeedMetal.getInt();
        }

        ticksNeeded = Util.getTicksNeededProgress(blockObject.hardness, speed, ticksElapsed, progressMade);
    }

    public boolean timeoutExceeded() {
        return this.ticksNeeded > 0 && this.ticksElapsed >= this.ticksNeeded;

    }

    public int ticksNeeded() { return this.ticksNeeded; }

    public int ticksElapsed() { return this.ticksElapsed; }

    private void reset() {
        this.ticksElapsed = 0;
        this.progressMade = 0.0;

    }

    private void forceReset() {
        reset();
        this.tempPos = null;
    }

}


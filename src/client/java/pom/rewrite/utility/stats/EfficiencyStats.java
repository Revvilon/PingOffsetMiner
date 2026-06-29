package pom.rewrite.utility.stats;

import meteordevelopment.orbit.EventHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import pom.rewrite.events.blockBroken;
import pom.rewrite.features.gui.EfficiencyDisplay;
import pom.rewrite.utility.Util;
import pom.rewrite.utility.block.BlockData;
import pom.rewrite.utility.block.BlockObject;

import java.util.HashMap;

public class EfficiencyStats {
    private static final EfficiencyStats INSTANCE = new EfficiencyStats();
    public static EfficiencyStats getInstance() { return INSTANCE; }
    private EfficiencyStats() {}

    MiningStats miningStats = MiningStats.instance();
    BlockData blockData = BlockData.getInstance();

    private long timeStarted = 0;
    private final HashMap<String, Integer> maxBlocksMined = new HashMap<>();
    private int ticksElapsed = 0;
    private long lastTimeMined = 0;

    public void init() {
        ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
            if (mc.player == null || mc.level == null) return;

            BlockObject block = blockData.getLastMined();

            if (!isMining() || block == null) {
                reset();
                return;
            }
            this.ticksElapsed++;
            int ticksNeeded = Util.getTicksNeededNoOffset(block, miningStats.getMiningSpeed());

            if (ticksElapsed >= ticksNeeded) {
                this.maxBlocksMined.merge(block.id, 1, Integer::sum);
                ticksElapsed = 0;
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((listener, mc) -> {
            reset();
        });
    }

    public float getUptime() {
        if (!isMining()) return 0;
        float difference = System.currentTimeMillis() - this.timeStarted;
        return difference / 1000f;
    }

    public int getEfficiency() {
        if (blockData.getMinedBlocks().isEmpty() || this.maxBlocksMined.isEmpty()) return 100;
        float eff = (float) Util.getTotalMined(blockData.getMinedBlocks()) / Util.getTotalMined(this.maxBlocksMined);

        return Math.round(eff * 100f);
    }

    public boolean isMining() {
        long currentTime = System.currentTimeMillis();

        return currentTime < (this.lastTimeMined + EfficiencyDisplay.timeout.getInt() * 1000L);
    }

    @EventHandler
    private void blockBroken(blockBroken event) {
        if (!isMining()) {
            this.timeStarted = System.currentTimeMillis();
        }
        this.lastTimeMined = System.currentTimeMillis();

    }

    private void reset() {
        this.timeStarted = 0;
        this.maxBlocksMined.clear();
        this.ticksElapsed = 0;
        this.lastTimeMined = 0;
    }
}

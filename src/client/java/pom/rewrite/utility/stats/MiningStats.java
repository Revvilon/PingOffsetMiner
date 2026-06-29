package pom.rewrite.utility.stats;

import net.minecraft.world.item.ItemStack;
import pom.rewrite.features.debug.CustomStats;
import pom.rewrite.features.render.MsbRender;
import pom.rewrite.utility.block.BlockData;
import pom.rewrite.utility.block.BlockObject;
import pom.rewrite.utility.server.IslandUtils;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class MiningStats {
    private ItemStack item = ItemStack.EMPTY;
    private int miningSpeed = 0;

    private boolean msbActive = false;
    private int msbCooldown = 0;
    private float msbMultiplier = 1.0f;

    private static final MiningStats instance = new MiningStats();
    public static MiningStats instance() {return instance;}
    private MiningStats() {}

    public void setItem(ItemStack item) { this.item = (item == null) ? ItemStack.EMPTY : item; }
    public ItemStack getItem() { return this.item; }

    public void setMiningSpeed(int miningSpeed) { this.miningSpeed = miningSpeed; }
    public int getMiningSpeed() { return CustomStats.instance.isEnabled() ? CustomStats.customSpeed.getInt() : miningSpeed; }

    public void setMsbActive(boolean msbActive) { this.msbActive = msbActive; }
    public boolean getMsbActive() {return msbActive; }

    public void setMsbCooldown(int msbCooldown) { this.msbCooldown = msbCooldown; }
    public int getMsbCooldown() { return msbCooldown; }

    public void setMsbMultiplier(float msbMultiplier) { this.msbMultiplier = msbMultiplier; }
    public float getMsbMultiplier() { return msbMultiplier; }

    public boolean isActive() {
        return this.item != ItemStack.EMPTY;
    }

    public boolean shouldRender() {
        if (CustomStats.instance.isEnabled()) return true;

        if (!(BlockData.getInstance().getCurrentBlock() instanceof BlockObject) || !IslandUtils.isIsland() || TickStats.instance().ticksNeeded() <= 0) {
            return false;
        }

        if (!isActive()) return false;

        if (MsbRender.instance.isEnabled()) {
            return getMsbActive() == MsbRender.msbToggle.getBool();
        }

        return true;
    }

    public <T> void ifReady(T value, Predicate<T> check, Consumer<T> task) {
        if (this.isActive() && check.test(value)) {
            task.accept(value);
        }
    }

    public void reset() {
        this.item = ItemStack.EMPTY;
        this.miningSpeed = 0;
        this.msbActive = false;
        this.msbCooldown = 0;
        this.msbMultiplier = 1.0f;
    }
}

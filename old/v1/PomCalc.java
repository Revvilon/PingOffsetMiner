package pom.v1;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import pom.v1.events.worldTickEvent;
import pom.v1.pomGetter.SpeedCalc;

import static pom.v1.PingOffsetMinerClient.*;
import static pom.v1.PomConfig.PomConfig.Config;

public class PomCalc {

    private double ticksNeeded = -1;
    private int ticksElapsed = 0;

    public void setTicksNeeded(double ticksNeeded) {this.ticksNeeded = ticksNeeded;}


    public boolean timeoutExceeded() {
        return this.ticksNeeded > 0 && this.ticksElapsed >= this.ticksNeeded && Minecraft.getInstance().options.keyAttack.isDown();
    }

    public void reset() {
        this.ticksNeeded = -1;
        this.ticksElapsed = 0;
    }

    public int getTicksNeeded() {

        double debugSpeed = getSpeed();

        ticksNeeded = SpeedCalc.getTicksToBreak((int) POM_BLOCK.getHardness(), debugSpeed);

        double debugTps = getTPS();
        double pingSec = getPing() / 1000.0;

        double pingMath = ticksNeeded - pingSec * debugTps;

        double pingOffset = ticksNeeded - pingMath > pingMath
                ? ticksNeeded - pingMath
                : ticksNeeded;

        return (int) Math.max(4, pingOffset);
    }
    public int getTicksElapsed() { return this.ticksElapsed; }

    public void incrementTicksElapsed() {
        if (this.ticksNeeded <= 0) {
            ticksElapsed = 0;
            return;
        }
        this.ticksElapsed++;
    }

    public double getSpeed() {
        double debugSpeed = Config().debug.get() ? Config().speed.value : TOOL_STATS.getSpeed();
        double extra = Config().extra.get() ? Config().extraVal.get() : 0;

        if (POM_BLOCK.getName().contains("gem")) {
            debugSpeed = debugSpeed + extra;
        }
        return debugSpeed;
    }
}

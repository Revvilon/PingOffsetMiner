package pom.v1;

import meteordevelopment.orbit.EventHandler;
import pom.v1.events.worldTickEvent;
import pom.v1.pomGetter.SpeedCalc;

import java.util.HashMap;

import static pom.v1.PingOffsetMinerClient.*;
import static pom.v1.PomConfig.PomConfig.Config;

public class PomEfficiency {

    private final HashMap<String, Integer> maxBlocksMined = new HashMap<>();
    private long lastMined;
    public int breakTick;
    public float efficiency;
    public long upTime;

    public PomEfficiency() {
        resetVals();
    }

    public void calculateBlocks() {
        String block = POM_BLOCK.lastMinedBlock;

        if (!this.isMining() || block.isBlank()) {
            this.upTime = System.currentTimeMillis();
            this.resetVals();
            return;
        }
        breakTick++;
        int ticksNeeded = (int) SpeedCalc.getTicksToBreak(SpeedCalc.blockHardness.get(block), POM_CALC.getSpeed());

        if (breakTick >= ticksNeeded) {
            this.maxBlocksMined.merge(block, 1, Integer::sum);
            breakTick -= ticksNeeded;
        }

        efficiency = (float) POM_BLOCK.getTotalMined(POM_BLOCK.getMinedBlocks()) / POM_BLOCK.getTotalMined(this.maxBlocksMined);
    }

    public HashMap<String, Integer> getMaxBlocksMined() {return this.maxBlocksMined;}

    public float getEfficiency() {
        return efficiency != 0 ? Math.clamp(efficiency, 0.0f, 1.0f) : 1.0f;
    }

    public void setMining(long lastMined) {
        if (!isMining()) this.setUpTime(lastMined);
        this.lastMined = lastMined;
    }
    public boolean isMining() {
        long currentTime = System.currentTimeMillis();

        return currentTime < (this.lastMined + Config().efficiencyDisplaySec.get() * 1000L);
    }

    public double getUpTime() {
        if (!isMining()) return 0.0;
        long difference = System.currentTimeMillis() - this.upTime;

        return difference / 1000.0;
    }
    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public void resetVals() {
        this.lastMined = 0;
        this.breakTick = 0;
        this.efficiency = 0f;
        POM_BLOCK.resetBlocksMined();
        SpeedCalc.blockHardness.forEach((key, value) -> this.maxBlocksMined.put(key, 0));
        upTime = 0;
    }

    public float tempEff;
    public void setTempEff(float efficiency) {
        tempEff = efficiency;
    }

    public float getTempEff() {return tempEff;}


    public int ticksUp = 10;

    public void onWorldTick() {
        this.calculateBlocks();

        if (POM_EFF.isMining()) {
            ticksUp++;

            if (ticksUp >= 5) {
                setTempEff(getEfficiency());
                ticksUp = 0;
            }
        } else {
            setTempEff(1.0f);
        }
    }
}

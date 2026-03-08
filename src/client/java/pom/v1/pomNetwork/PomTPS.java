package pom.v1.pomNetwork;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.chat.Component;
import pom.v1.Util;
import pom.v1.events.gameJoinedEvent;
import pom.v1.events.tpsReceivedEvent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PomTPS {

    private ConcurrentLinkedQueue<Float> tickIntervals =  new ConcurrentLinkedQueue<>();
    private final int MAX = 20;
    private long lastUpdate = -1;

    private void addLatency(long realTime) {
        long timeElapsed = realTime - lastUpdate;

        if (timeElapsed < 100) return;

        tickIntervals.add((float) timeElapsed);

        while (tickIntervals.size() > MAX) {
            tickIntervals.poll();
        }

        lastUpdate = realTime;
    }

    public double getAverageLatency() {
        if  (tickIntervals.isEmpty()) return 20.0;

        double averageTicks = tickIntervals.stream()
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(1000.0);

        double tps = 20000.0 / averageTicks;

        return Math.min(20.0, tps);
    }

    @EventHandler
    public void onTPS(tpsReceivedEvent event) {
        addLatency(event.time);
    }
    @EventHandler
    public void gameJoin(gameJoinedEvent event) {
        tickIntervals.clear();
        lastUpdate = System.currentTimeMillis();
    }
}

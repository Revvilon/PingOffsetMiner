package pom.rewrite.utility.server;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import pom.rewrite.events.gameJoined;
import pom.rewrite.events.packetReceived;
import pom.rewrite.features.debug.CustomStats;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerStats {
    private static final ConcurrentLinkedQueue<Long> pingIntervals =  new ConcurrentLinkedQueue<>();

    static final int MAX_LATENCIES = 20;

    public static void addPing(long latency) {
        pingIntervals.add(latency);
        while (pingIntervals.size() > MAX_LATENCIES) {
            pingIntervals.poll();
        }
    }

    public static long getPing() {
        if (CustomStats.instance.isEnabled()) return CustomStats.customPing.getInt();
        List<Long> latencyList = pingIntervals.stream().toList();
        long sum = latencyList.stream().mapToLong(Long::longValue).sum();
        return latencyList.isEmpty() ? 0 : sum / latencyList.size();
    }

    private static long lastServerTickTime = -1L;
    private static long lastWorldSwitchTime = 0L;
    private static int lastPingParameter = 0;
    private static long totalServerTicks = 0L;

    private static final Map<Long, Double> msPerTickList = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Double> eldest) {
            return size() > 100;
        }
    };

    public static Double getTps() {
        if (CustomStats.instance.isEnabled()) return CustomStats.customTps.getDouble();
        long now = System.currentTimeMillis();
        if (now - lastWorldSwitchTime < 5000) {
            return 20.0;
        }

        synchronized (msPerTickList) {
            if (msPerTickList.isEmpty() || (lastServerTickTime != -1L && (now - lastServerTickTime) >= 1000)) {
                return 0.0;
            }
            double averageMs = Arrays.stream(msPerTickList.values().toArray())
                    .mapToDouble(val -> (Double) val)
                    .average()
                    .orElse(0.0);

            if (averageMs == 0.0) return  0.0;

            return Math.max(0.0, Math.min(20.0, 1000.0 / averageMs));
        }
    }

    @EventHandler
    private static void onPing(packetReceived event) {
        if (event.value instanceof ClientboundPingPacket packet) {
            if (packet.getId() == lastPingParameter) return;

            lastPingParameter = packet.getId();

            totalServerTicks++;

            long now = System.currentTimeMillis();
            if (lastServerTickTime != -1L) {
                double duration = now - lastServerTickTime;
                synchronized (msPerTickList) {
                    msPerTickList.put(now, duration);
                }
            }

            lastServerTickTime = now;
        }
    }

    @EventHandler
    private static void onWorldChange(gameJoined event) {
        synchronized (msPerTickList) {
            msPerTickList.clear();
        }
        lastServerTickTime = -1;
        lastWorldSwitchTime = System.currentTimeMillis();
    }
}

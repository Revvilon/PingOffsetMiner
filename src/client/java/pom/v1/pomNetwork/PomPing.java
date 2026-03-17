package pom.v1.pomNetwork;

import com.google.common.util.concurrent.UncheckedTimeoutException;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.debugchart.LocalSampleLogger;
import pom.v1.PingOffsetMinerClient;
import pom.v1.Util;
import pom.v1.events.pingReceivedEvent;
import pom.v1.events.worldTickEvent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PomPing {

    /// CODE BELOW TAKEN FROM PROPERERPING
    /// https://github.com/freegamerskids/PropererPing

    public static final int ID =  (PingOffsetMinerClient.MOD_ID.hashCode());
    private static long lastPing = -1;

    ConcurrentLinkedQueue<Long> latencies =  new ConcurrentLinkedQueue<>();

    final int MAX_LATENCIES = 20;

    private void addLatency(long latency) {
        latencies.add(latency);
        while (latencies.size() > MAX_LATENCIES) {
            latencies.poll();
        }
    }

    public long getAverageLatency() {
        List<Long> latencyList = latencies.stream().toList();
        long sum = latencyList.stream().mapToLong(Long::longValue).sum();
        return latencyList.isEmpty() ? 0 : sum / latencyList.size();
    }

    public void clear() {
        latencies.clear();
    }

    @EventHandler
    public void pingReceivedEvent(pingReceivedEvent event) {
        addLatency(event.time);
    }
}

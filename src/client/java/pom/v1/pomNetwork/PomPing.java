package pom.v1.pomNetwork;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import pom.v1.events.pingReceivedEvent;
import pom.v1.events.tpsReceivedEvent;
import pom.v1.events.worldTickEvent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class PomPing {

    /// CODE BELOW TAKEN FROM PROPERERPING
    /// https://github.com/freegamerskids/PropererPing

    ConcurrentLinkedQueue<Long> latencies =  new ConcurrentLinkedQueue<>();

    final int MAX_LATENCIES = 5;

    void receivedPacket(long startTime) {
        long currentTime = System.currentTimeMillis();
        long latency = currentTime - startTime;
        addLatency(latency);
    }

    private void addLatency(long latency) {
        latencies.add(latency);
        while (latencies.size() > MAX_LATENCIES) {
            latencies.poll();
        }
    }

    public long getAverageLatency() {
        List<Long> latencyList = latencies.stream().toList();
        long sum = latencyList.stream().mapToLong(Long::longValue).sum();
        return sum / latencyList.size();
    }

    public void clear() {
        latencies.clear();
    }

    void sendQueryPing(Minecraft client) {
        if (client.getConnection() != null) {
            long startTime = System.currentTimeMillis();
            ServerboundPingRequestPacket packet = new ServerboundPingRequestPacket(startTime);
            client.getConnection().send(packet);
        }
    }

    private int tickCount = 0;
    @EventHandler
    public void tick(worldTickEvent event) {
        tickCount++;
        if (tickCount > 10) {
            tickCount = 0;
            sendQueryPing(Minecraft.getInstance());
        }
    }

    @EventHandler
    public void pingReceivedEvent(pingReceivedEvent event) {
        receivedPacket(event.time);
    }
}

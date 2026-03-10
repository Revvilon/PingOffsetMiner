package pom.v1.pomNetwork;

import meteordevelopment.orbit.EventHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.resources.Identifier;
import pom.v1.PingOffsetMinerClient;
import pom.v1.Util;
import pom.v1.events.pingReceivedEvent;
import pom.v1.events.worldTickEvent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PomPing {

    /// CODE BELOW TAKEN FROM PROPERERPING
    /// https://github.com/freegamerskids/PropererPing

    public static final long ID = (long) -(PingOffsetMinerClient.MOD_ID.hashCode()) * 20;
    private static long lastPing = -1;

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
        return latencyList.isEmpty() ? 0 : sum / latencyList.size();
    }

    public void clear() {
        latencies.clear();
    }

    void sendQueryPing(Minecraft client) {
        if (client.getConnection() != null) {
            ServerboundPingRequestPacket packet = new ServerboundPingRequestPacket(ID);
            lastPing = System.currentTimeMillis();
            client.getConnection().send(packet);
        }
    }

    private int tickCount = 0;
    @EventHandler
    public void tick(worldTickEvent event) {
        tickCount++;
        if (tickCount > 20) {
            tickCount = 0;
            sendQueryPing(Minecraft.getInstance());
        }
    }

    @EventHandler
    public void pingReceivedEvent(pingReceivedEvent event) {
        if (lastPing != -1) {
            receivedPacket(lastPing);
        }
    }
}

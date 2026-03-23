package pom.v1.pomNetwork;

import meteordevelopment.orbit.EventHandler;
import pom.v1.events.pingReceivedEvent;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PomPing {


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

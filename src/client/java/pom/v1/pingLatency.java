package pom.v1;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class pingLatency {
    private final ConcurrentLinkedQueue<Long> latencies = new ConcurrentLinkedQueue<>();

    private static final int MAX_LATENCIES = 5;

    public void recordPacketReceived(long startTime) {
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

    public Long getAverageLatency() {
        List<Long> latencyList = latencies.stream().toList();
        if (!latencyList.isEmpty()) {
            long sum = 0;
            for (Long latency : latencyList) {
                sum += latency;
            }
            return sum / latencyList.size();
        } else {
            return null;
        }
    }

    public void clear() {
        latencies.clear();
    }
}

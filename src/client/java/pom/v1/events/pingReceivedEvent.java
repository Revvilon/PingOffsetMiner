package pom.v1.events;

import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;

public class pingReceivedEvent {
    public long time;

    public pingReceivedEvent(long time) {
        this.time = time;
    }
}

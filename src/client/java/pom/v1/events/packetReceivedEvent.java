package pom.v1.events;

import net.minecraft.network.protocol.Packet;

public class packetReceivedEvent {
    public Packet<?> value;

    public packetReceivedEvent(Packet<?> value) { this.value = value; }
}

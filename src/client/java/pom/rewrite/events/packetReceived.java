package pom.rewrite.events;

import net.minecraft.network.protocol.Packet;

public class packetReceived {
    public Packet<?> value;

    public packetReceived(Packet<?> value) { this.value = value; }
}
package pom.rewrite.events;

import net.minecraft.network.protocol.Packet;

public class packetSent {
    Packet<?> packet;
    public packetSent(Packet<?> packet) {
        this.packet = packet;
    }
}

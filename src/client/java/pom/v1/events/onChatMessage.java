package pom.v1.events;

import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

public class onChatMessage {
    public ClientboundSystemChatPacket packet;
    public String message;

    public onChatMessage(ClientboundSystemChatPacket packet, String message) {
        this.packet = packet;
        this.message = message;
    }
}

package pom.v1.events;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

public class onTabUpdate {
    public ClientboundPlayerInfoUpdatePacket packet;
    public String string;

    public onTabUpdate(ClientboundPlayerInfoUpdatePacket packet, String string) {
        this.string = string;
        this.packet = packet;
    }
}

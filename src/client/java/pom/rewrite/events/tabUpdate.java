package pom.rewrite.events;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

import java.util.List;

public class tabUpdate {
    public List<ClientboundPlayerInfoUpdatePacket.Entry> entries;

    public tabUpdate(List<ClientboundPlayerInfoUpdatePacket.Entry> entries) {
        this.entries = entries;
    }
}

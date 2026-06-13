package pom.v1.mixin;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.PingOffsetMinerClient;
import pom.v1.events.tpsReceivedEvent;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {

    @Inject(method = "handlePing", at = @At("HEAD"))
    private void onHandlePing(ClientboundPingPacket packet, CallbackInfo ci) {
        long time = System.currentTimeMillis();
        tpsReceivedEvent event = new tpsReceivedEvent(time);
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }
}

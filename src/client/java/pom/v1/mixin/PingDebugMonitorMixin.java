package pom.v1.mixin;

import net.minecraft.client.multiplayer.PingDebugMonitor;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.PingOffsetMinerClient;
import pom.v1.events.pingReceivedEvent;

@Mixin(PingDebugMonitor.class)
public class PingDebugMonitorMixin {
    @Inject(method = "onPongReceived", at = @At("TAIL"))
    private void onPongReceivedEvent(ClientboundPongResponsePacket packet, CallbackInfo ci) {
        long delta = Util.getMillis() - packet.time();
        pingReceivedEvent event = new pingReceivedEvent(delta);
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }
}

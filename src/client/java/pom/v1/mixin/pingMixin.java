package pom.v1.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.Config;
import pom.v1.PingOffsetMinerClient;


@Mixin(ClientPlayNetworkHandler.class)
public class pingMixin {


    @Inject(method = "onPingResult(Lnet/minecraft/network/packet/s2c/query/PingResultS2CPacket;)V", at = @At("HEAD"), remap = false)
    private void onPing(PingResultS2CPacket packet, CallbackInfo ci) {
        PingOffsetMinerClient.onPingResultReceived(packet);
} }

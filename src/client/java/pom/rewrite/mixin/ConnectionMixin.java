package pom.rewrite.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.rewrite.PingOffsetMinerClient;
import pom.rewrite.events.packetReceived;
import pom.rewrite.events.packetSent;

@Mixin(value = Connection.class, priority = 500)
public abstract class ConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V"), cancellable = true)
    private void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        packetReceived event =  new packetReceived(packet);
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"))
    private void send(Packet<?> packet, CallbackInfo ci) {
        packetSent event = new  packetSent(packet);
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }
}
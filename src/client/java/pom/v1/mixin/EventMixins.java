package pom.v1.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.PingOffsetMinerClient;
import pom.v1.Util;
import pom.v1.events.*;

@Mixin(ClientPacketListener.class)
public class EventMixins {
    @Inject(method = "handleContainerContent", at = @At("TAIL"))
    private void onJoinWorld(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack held = player.getMainHandItem();

            onHeldSlot event = new onHeldSlot(held);
            PingOffsetMinerClient.EVENT_BUS.post(event);
        }
    }

    @Inject(method = "handleSystemChat", at = @At("HEAD"))
    private void onHandleSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
        Minecraft.getInstance().execute(() -> {
            onChatMessage event = new onChatMessage(
                    packet,
                    packet.content().getString()
            );
            PingOffsetMinerClient.EVENT_BUS.post(event);
        });
    }

    @Inject(method = "handleSetTime", at = @At("HEAD"))
        private void onHandleSetTime(ClientboundSetTimePacket packet, CallbackInfo ci) {
            if (!(packet instanceof ClientboundSetTimePacket)) return;

            long time = System.currentTimeMillis();
            tpsReceivedEvent event = new tpsReceivedEvent(time);
            PingOffsetMinerClient.EVENT_BUS.post(event);
        }

        @Inject(method = "handleLogin", at = @At("TAIL"))
        private void onGameJoin(ClientboundLoginPacket packet, CallbackInfo ci) {
            gameJoinedEvent event = new gameJoinedEvent();
            PingOffsetMinerClient.EVENT_BUS.post(event);
        }

        @Inject(method = "handlePlayerInfoUpdate", at = @At("TAIL"))
        private void onHandlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
            Util.updateTab();
            for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
                if (entry.displayName() != null) {
                    String name = entry.displayName().getString();
                    if (Util.convertSpeed(name) > -1) {
                        onSpeedUpdate event = new onSpeedUpdate(
                                Util.convertSpeed(name)
                        );
                        PingOffsetMinerClient.EVENT_BUS.post(event);
                    }
                    if (name.contains("Boost: Available")) {
                        onTabUpdate event = new onTabUpdate(packet, name);
                        PingOffsetMinerClient.EVENT_BUS.post(event);
                    }
                }
            }
        }

        @Inject(method = "tick", at = @At("TAIL"))
        private void onTick(CallbackInfo ci) {
            worldTickEvent event = new worldTickEvent();
            PingOffsetMinerClient.EVENT_BUS.post(event);
        }

        @Inject(method = "handleSetPlayerInventory", at = @At("TAIL"))
        private void onLoad(CallbackInfo ci) {
            if (Minecraft.getInstance().player != null) {
                onHeldSlot event = new onHeldSlot(
                        Minecraft.getInstance().player.getActiveItem()
                );
                PingOffsetMinerClient.EVENT_BUS.post(event);
            }
            for (String name : Util.getTabList()) {
                if (Util.convertSpeed(name) > -1) {
                    onSpeedUpdate event = new onSpeedUpdate(
                            Util.convertSpeed(name)
                    );
                    PingOffsetMinerClient.EVENT_BUS.post(event);
                }
            }
        }

        @Inject(method = "handlePongResponse(Lnet/minecraft/network/protocol/ping/ClientboundPongResponsePacket;)V", at = @At("HEAD"))
        private void onPing(ClientboundPongResponsePacket packet, CallbackInfo ci) {
            pingReceivedEvent event = new pingReceivedEvent(packet.time());
            PingOffsetMinerClient.EVENT_BUS.post(event);
        }

}
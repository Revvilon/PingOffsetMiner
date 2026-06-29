package pom.rewrite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.rewrite.PingOffsetMinerClient;
import pom.rewrite.events.chatEvent;
import pom.rewrite.events.gameJoined;
import pom.rewrite.events.heldSlotUpdate;
import pom.rewrite.events.tabUpdate;

import java.util.ArrayList;

@Mixin(ClientPacketListener.class)
public class ClientMixin {

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showNetworkCharts()Z"))
    private boolean shouldSendPing(boolean original) {
        return true;
    }

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void onGameJoin(ClientboundLoginPacket packet, CallbackInfo ci) {
        gameJoined event = new gameJoined();
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }

    @Inject(method = "handlePlayerInfoUpdate", at = @At("TAIL"))
    private void onPlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        tabUpdate event = new tabUpdate(packet.entries());
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }

    @Inject(method = "handleSetPlayerInventory", at = @At("TAIL"))
    private void onSetPlayerInventory(CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) return;

        ItemStack item = Minecraft.getInstance().player.getActiveItem();
        heldSlotUpdate event = new heldSlotUpdate(item);
        PingOffsetMinerClient.EVENT_BUS.post(event);

        tabUpdate tabEvent = new tabUpdate(new ArrayList<>());
        PingOffsetMinerClient.EVENT_BUS.post(tabEvent);
    }

    @Inject(method = "handleContainerContent", at = @At("TAIL"))
    private void onContainerContent(CallbackInfo ci) {
        if (Minecraft.getInstance().player == null) return;
        ItemStack item = Minecraft.getInstance().player.getActiveItem();
        heldSlotUpdate event = new heldSlotUpdate(item);
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }

    @Inject(method = "handleSystemChat", at = @At("TAIL"))
    private void onSystemChat(ClientboundSystemChatPacket message,  CallbackInfo ci) {
        chatEvent event = new chatEvent(message.content().getString().replaceAll("$.", ""));
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }

}

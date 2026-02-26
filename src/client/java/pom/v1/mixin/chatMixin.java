package pom.v1.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class chatMixin {

    @Inject(method = "handleSystemChat", at = @At("HEAD"))
    private void onHandleSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {
      /*  String chatMessage = packet.toString();
        if (pomConfig.HANDLER.instance().ability && chatMessage.contains("Mining Speed Boost") && chatMessage.contains("Pickaxe Ability!")) {
            pomConfig.HANDLER.instance().active = false;
        }
        if (pomConfig.HANDLER.instance().ability && chatMessage.contains("Your Mining Speed Boost has expired!")) {
            pomConfig.HANDLER.instance().active = true;
        };*/
    }
}
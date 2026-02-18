package pom.v1.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.modmenu.pomConfig;

@Mixin(ClientPlayNetworkHandler.class)
public class chatMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String chatMessage = packet.toString();
        if (pomConfig.HANDLER.instance().ability && chatMessage.contains("GameMessageS2CPacket[content=empty[style={!italic}, siblings=[literal{You used your }[style={color=green}], literal{Mining Speed Boost }[style={color=gold}], literal{Pickaxe Ability!}[style={color=green}]]], overlay=false]")) {
            pomConfig.HANDLER.instance().active = false;
        }
        if (pomConfig.HANDLER.instance().ability && chatMessage.contains("GameMessageS2CPacket[content=empty[style={!italic}, siblings=[literal{Your Mining Speed Boost has expired!}[style={color=red}]]], overlay=false]")) {

            pomConfig.HANDLER.instance().active = true;
        };
    }
}
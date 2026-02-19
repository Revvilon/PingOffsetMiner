package pom.v1.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.Util;
import pom.v1.modmenu.pomConfig;

@Mixin(ClientPlayNetworkHandler.class)
public class chatMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        String chatMessage = packet.toString();
        if (pomConfig.HANDLER.instance().ability && chatMessage.contains("Mining Speed Boost") && chatMessage.contains("Pickaxe Ability!")) {
            pomConfig.HANDLER.instance().active = false;
        }
        if (pomConfig.HANDLER.instance().ability && chatMessage.contains("Your Mining Speed Boost has expired!")) {
            pomConfig.HANDLER.instance().active = true;
        };
    }
}
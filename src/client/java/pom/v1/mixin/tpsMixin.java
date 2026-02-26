package pom.v1.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.Util;

@Mixin(ClientPacketListener.class)
public class tpsMixin {
    @Unique
    private static long lastRealTime = -1;
    @Unique
    private static long lastWorldAge = -1;
    @Unique
    private static double estTps = 20.0;

    @Inject(method = "handleSetTime", at = @At("HEAD"))
    private void onHandleSetTime(ClientboundSetTimePacket packet, CallbackInfo ci) {
        long currentRealTime = System.currentTimeMillis();
        long currentWorldAge = packet.gameTime();

        if (lastWorldAge != -1 && lastRealTime != -1) {
            long tickDiff = currentWorldAge - lastWorldAge;
            long timeDiff = currentRealTime - lastRealTime;

            if (timeDiff > 0 && tickDiff > 0) {
                double tps = ( (double) tickDiff / (double) timeDiff) * 1000.0;

                Util.tps = Math.min(estTps, tps);
            }

        }
    lastWorldAge = currentWorldAge;
        lastRealTime = currentRealTime;
    }
}
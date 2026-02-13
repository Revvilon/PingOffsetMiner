package pom.v1.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.PingOffsetMinerClient;

@Mixin(ClientPlayNetworkHandler.class)
public class tpsMixin {
    private static long lastRealTime = -1;
    private static long lastWorldAge = -1;
    private static double estTps = 20.0;

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        long currentRealTime = System.currentTimeMillis();
        long currentWorldAge = packet.time();

        if (lastWorldAge != -1 && lastRealTime != -1) {
            long tickDiff = currentWorldAge - lastWorldAge;
            long timeDiff = currentRealTime - lastRealTime;

            if (timeDiff > 0 && tickDiff > 0) {
                double tps = ( (double) tickDiff / (double) timeDiff) * 1000.0;

                PingOffsetMinerClient.tps = Math.min(estTps, tps);
            }

        }
    lastWorldAge = currentWorldAge;
        lastRealTime = currentRealTime;
    }
}
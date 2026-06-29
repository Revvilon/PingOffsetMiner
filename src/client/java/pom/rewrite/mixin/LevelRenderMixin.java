package pom.rewrite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import pom.rewrite.features.render.ProgressRender;
import pom.rewrite.utility.stats.MiningStats;
import pom.rewrite.utility.stats.TickStats;


@Mixin(LevelRenderer.class)
public abstract class LevelRenderMixin {

    @ModifyExpressionValue(
            method = "submitBlockDestroyAnimation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/state/level/BlockBreakingRenderState;progress()I")
    )
    private int modifyMiningStage(int stage) {
        if (stage < 0 || stage > 9) return stage;

        if (MiningStats.instance().shouldRender() && ProgressRender.instance.isEnabled()) {
            double progress = ((double)TickStats.instance().ticksElapsed() / (double)TickStats.instance().ticksNeeded()) * 100.0;
            return getStageFromProgress(progress);
        }

        return stage;
    }

    @Unique
    private int getStageFromProgress(double progress) {
        double clampedProgress = Math.clamp(progress, 0.0, 100.0);
        return 1 + (int) ((clampedProgress / 100.0) * 8.0);
    }
}

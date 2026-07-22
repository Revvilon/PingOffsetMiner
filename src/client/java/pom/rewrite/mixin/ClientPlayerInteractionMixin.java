package pom.rewrite.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pom.rewrite.PingOffsetMinerClient;
import pom.rewrite.events.startBreak;
import pom.rewrite.features.debug.HSMModern;
import pom.rewrite.utility.Util;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionMixin {

    @Shadow
    private ItemStack destroyingItem;

    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    private BlockPos destroyBlockPos;

    @Inject(at = @At("RETURN"), method = "sameDestroyTarget", cancellable = true)
    private void itemStackCompare(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!HSMModern.instance.isEnabled() || minecraft.player == null || destroyingItem == null) {
            return;
        }
        ItemStack hand = minecraft.player.getMainHandItem();

        if (pos.equals(this.destroyBlockPos) && hand.is(destroyingItem.getItem())) {

            if (ItemStack.isSameItem(hand, destroyingItem) || Util.compareComponents(hand.getComponents(), destroyingItem.getComponents())) {

                this.destroyingItem = hand;
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "startDestroyBlock")
    private void startDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (minecraft.player != null) {
            startBreak event = new  startBreak(minecraft.player.tickCount);
            PingOffsetMinerClient.EVENT_BUS.post(event);
        }

    }
}

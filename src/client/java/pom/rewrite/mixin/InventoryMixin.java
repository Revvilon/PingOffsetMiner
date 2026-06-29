package pom.rewrite.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.rewrite.PingOffsetMinerClient;
import pom.rewrite.events.heldSlotUpdate;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "setSelectedSlot", at = @At("TAIL"))
    private void onSlotSelected(int slot, CallbackInfo ci) {
        Inventory inv = (Inventory)(Object)this;
        ItemStack held = inv.getItem(slot);
        heldSlotUpdate event = new heldSlotUpdate(held);
        PingOffsetMinerClient.EVENT_BUS.post(event);
    }
}

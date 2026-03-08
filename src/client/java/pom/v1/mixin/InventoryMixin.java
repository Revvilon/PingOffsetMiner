package pom.v1.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pom.v1.PingOffsetMinerClient;
import pom.v1.events.onHeldSlot;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "setSelectedSlot", at = @At("TAIL"))
    private void onSlotSelected(int slot, CallbackInfo ci) {
        Inventory inv =  (Inventory)(Object)this;
        ItemStack held = inv.getItem(slot);

        PingOffsetMinerClient.EVENT_BUS.post(new onHeldSlot(held));
    }
}

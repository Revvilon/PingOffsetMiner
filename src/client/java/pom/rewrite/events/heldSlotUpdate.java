package pom.rewrite.events;

import net.minecraft.world.item.ItemStack;

public class heldSlotUpdate {
    public ItemStack item;
    public heldSlotUpdate(ItemStack stack) { this.item = stack; }
}

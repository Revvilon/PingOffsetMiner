package pom.v1.events;

import net.minecraft.world.item.ItemStack;

public class onHeldSlot {
    public ItemStack held;
    public onHeldSlot(ItemStack held) {
        this.held = held;
    }
}

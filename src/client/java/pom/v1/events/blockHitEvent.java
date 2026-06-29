package pom.v1.events;

import net.minecraft.core.BlockPos;

public class blockHitEvent {
    public BlockPos pos;
    public String name;

    public blockHitEvent(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }
}

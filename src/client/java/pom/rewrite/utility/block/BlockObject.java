package pom.rewrite.utility.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BlockObject {
        public List<String> block;
        public String id;
        public int hardness;

        public transient BlockPos pos;
        public transient BlockState state;
}

package pom.v1.pomGetter;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import pom.v1.PomConfig.PomConfig;
import pom.v1.Util;

public class PomBlockData {

    public static class PomBlock {
        private BlockPos pos;
        private VoxelShape shape;
        private long hardness;
        private String name;
        private Block block;

        public PomBlock() {
            this.pos = null;
            this.shape = null;
            this.hardness = -1;
            this.name = "";
            this.block = null;
        }

        public BlockPos getBlockPos() {return this.pos;}
        public VoxelShape getShape() {return this.shape;}
        public long getHardness() {return this.hardness;}
        public String  getName() {return this.name;}

        public void setBlock(Minecraft client) {
            HitResult hr = client.hitResult;
            if (hr == null || client.level == null) return;

            if (hr.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult)hr).getBlockPos();

                BlockState blockState = Minecraft.getInstance().level.getBlockState(blockPos);

                Block block = blockState.getBlock();
                if (block != this.block) {

                    String blockName = SpeedCalc.getBlockName(block);

                    if (block == Blocks.COBBLESTONE && Util.getArea().contains("Mineshaft")) blockName = SpeedCalc.getBlockName(Blocks.INFESTED_COBBLESTONE);

                    if (PomConfig.Config().blockEnabled.getOrDefault(blockName, false)) {
                        this.shape = blockState.getCollisionShape(client.level, blockPos);
                        this.pos = blockPos;
                        this.hardness = SpeedCalc.blockHardness.get(blockName);
                        this.name = blockName;
                        return;
                    }
                } else return;
            }
            this.pos = null;
            this.shape = null;
            this.hardness = -1;
            this.name = "";
            this.block = null;
        }

        public boolean isEmpty() {
            return this.shape == null && this.pos == null;
        }
    }
}

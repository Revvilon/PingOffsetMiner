package pom.v1.pomGetter;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import pom.v1.Util;
import pom.v1.events.blockBrokenEvent;
import pom.v1.events.blockHitEvent;
import pom.v1.events.gameJoinedEvent;
import pom.v1.events.packetReceivedEvent;

import java.util.HashMap;

import static pom.v1.PingOffsetMinerClient.POM_BLOCK;
import static pom.v1.PingOffsetMinerClient.POM_EFF;
import static pom.v1.PomConfig.PomConfig.Config;

public class PomBlockData {

    public static class PomBlock {
        private BlockPos pos;
        private VoxelShape shape;
        private long hardness;
        private String name;
        private Block block;
        private String lastBlockHit;

        private final HashMap<BlockPos, String> hitBlocks = new HashMap<>();
        private final HashMap<String, Integer> minedBlocks = new HashMap<>();
        public String lastMinedBlock;

        public PomBlock() {
            resetVals();
        }

        public BlockPos getBlockPos() {return this.pos;}
        public VoxelShape getShape() {return this.shape;}
        public long getHardness() {return this.hardness;}
        public String  getName() {return this.name;}
        public HashMap<BlockPos, String> getHitBlocks() {return this.hitBlocks;}
        public HashMap<String, Integer> getMinedBlocks() {return this.minedBlocks;}

        public void resetBlocksMined() {
            SpeedCalc.blockHardness.forEach((key, val) -> {
                this.minedBlocks.put(key, 0);
            });
        }

        public int getTotalMined(HashMap<String, Integer> map) {
            int count = 0;
            for (Integer i : map.values()) {
                count += i;
            }
            return count;
        }

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

                    if (Config().blockEnabled.getOrDefault(blockName, false)) {
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

        public void resetVals() {
            this.pos = null;
            this.shape = null;
            this.hardness = -1;
            this.name = "";
            this.block = null;
            this.hitBlocks.clear();
            this.lastMinedBlock = "";
            resetBlocksMined();
            this.lastBlockHit = "";
        }

        public void blockHit(String name) {
            this.lastBlockHit = name;
        }
        public String getLastBlockHit() {return this.lastBlockHit;}
    }

    @EventHandler
    public void onBlockMined(packetReceivedEvent event) {
        if (event.value instanceof ClientboundBlockUpdatePacket packet) {

            if (!packet.getBlockState().isAir()) return;

            var hitBlocks = POM_BLOCK.getHitBlocks();
            var minedBlocks = POM_BLOCK.getMinedBlocks();
            BlockPos blockPos = packet.getPos();

            if (hitBlocks.containsKey(blockPos)) {
                String block = hitBlocks.get(blockPos);

                minedBlocks.merge(block, 1, Integer::sum);

                POM_BLOCK.lastMinedBlock = block;

                POM_EFF.setMining(System.currentTimeMillis());

                hitBlocks.remove(blockPos);
            }
        }
    }

    @EventHandler
    public void onBlockHit(blockHitEvent event) {
        if (SpeedCalc.blockHardness.containsKey(event.name)) {
            POM_BLOCK.hitBlocks.put(event.pos, event.name);
            POM_BLOCK.blockHit(event.name);
        }
    }

    @EventHandler
    public void onLoad(gameJoinedEvent event) {
        POM_BLOCK.resetVals();
    }
}

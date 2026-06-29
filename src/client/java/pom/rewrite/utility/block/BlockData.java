package pom.rewrite.utility.block;

import meteordevelopment.orbit.EventHandler;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import pom.rewrite.PingOffsetMinerClient;
import pom.rewrite.events.blockBroken;
import pom.rewrite.events.gameJoined;
import pom.rewrite.events.packetReceived;
import pom.rewrite.features.toggles.BlockToggles;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockData {
    private static final BlockData INSTANCE = new BlockData();
    public static BlockData getInstance() {return INSTANCE;}
    private BlockData(){}

    private BlockObject currentBlock;

    private BlockObject lastHit;
    private BlockObject lastMined;

    private final ConcurrentHashMap<BlockPos, BlockObject> hitBlocks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> minedBlocks = new ConcurrentHashMap<>();

    private final Minecraft mc = Minecraft.getInstance();

    public void init() {
        LevelRenderEvents.START_MAIN.register(context -> {

            if (mc.level == null || mc.player == null) {
                this.currentBlock = null;
                return;
            }

            HitResult hitResult = mc.hitResult;

            if (hitResult instanceof BlockHitResult hr && hr.getType() == HitResult.Type.BLOCK) {


                BlockPos pos = hr.getBlockPos();
                BlockState state = mc.level.getBlockState(pos);

                if (this.currentBlock != null && pos.equals(this.currentBlock.pos) && state.equals(this.currentBlock.state)) {
                    return;
                }

                Block block = state.getBlock();

                String name = block.getDescriptionId().replace("block.minecraft.", "");
                name = name.replace("_pane", "");
                if (BlockToggles.blocks.isEnabled(name)) {
                    BlockObject blockObject = BlockUtil.getBlock(name);

                    blockObject.pos = pos;
                    blockObject.state = state;
                    this.currentBlock = blockObject;
                    return;
                }
            }
            this.currentBlock = null;
        });

        AttackBlockCallback.EVENT.register((player, level, i, pos, d ) -> {
            if (!level.isClientSide()) return InteractionResult.PASS;

            BlockObject block = BlockUtil.getBlock(level.getBlockState(pos).getBlock());
            if (block instanceof BlockObject blockObject) {
                lastHit = blockObject;
                hitBlocks.put(pos.immutable(), blockObject);
            }
            return InteractionResult.PASS;
        });
    }

    public BlockObject getCurrentBlock() {
        return this.currentBlock;
    }
    public BlockObject getLastHit() {return this.lastHit;}
    public BlockObject getLastMined() {return this.lastMined;}

    public Map<BlockPos, BlockObject> getHitBlocks() {return this.hitBlocks;}
    public Map<String, Integer> getMinedBlocks() {return this.minedBlocks;}

    @EventHandler
    public void gameJoined(gameJoined event) {
        reset();
    }

    @EventHandler
    public void blockBroken(packetReceived event) {
        if (event.value instanceof ClientboundBlockUpdatePacket packet) {
            BlockState state = packet.getBlockState();
            if (!state.isAir() && !state.is(Blocks.BEDROCK)) return;

            BlockPos pos = packet.getPos();
            BlockObject block = this.hitBlocks.remove(pos);

            if (block != null) {
                this.minedBlocks.merge(block.id, 1, Integer::sum);
                this.lastMined = block;

                blockBroken blockEvent = new blockBroken();
                PingOffsetMinerClient.EVENT_BUS.post(blockEvent);
            }
        }
    }

    private void reset() {
        this.lastHit = null;
        this.lastMined = null;
        this.hitBlocks.clear();
        this.minedBlocks.clear();
    }
}

package pom.rewrite.utility.block;

import com.google.common.reflect.TypeToken;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import pom.rewrite.utility.json.JsonUtil;

import java.util.Map;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class BlockUtil {

    public static HitResult hitResult() {
        return Minecraft.getInstance().hitResult;
    }

    public static BlockHitResult blockHitResult(HitResult result) {
        if (result instanceof BlockHitResult hr && hr.getType() == HitResult.Type.BLOCK) {
            return hr;
        }
        return null;
    }

    public static VoxelShape getShape(BlockState blockState, BlockPos pos) {
        if (Minecraft.getInstance().level == null) return Shapes.empty();
        return blockState.getShape(Minecraft.getInstance().level, pos);
    }
    public static VoxelShape getShape(BlockHitResult hr) {
        if (Minecraft.getInstance().level == null) return Shapes.empty();
        BlockState state = Minecraft.getInstance().level.getBlockState(hr.getBlockPos());
        return getShape(state, hr.getBlockPos());
    }


    private static final JsonUtil.DataRegistry<BlockObject> registry = new JsonUtil.DataRegistry<>();

    private static final Identifier BLOCKS = Identifier.fromNamespaceAndPath(MOD_ID, "data/blocks.json");

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> registry.loadFromAssets(
                BLOCKS,
                new TypeToken<>() {
                },
                block -> block.block
        ));

    }

    public static Map<String, BlockObject> getBlocks() {
        return registry.getMap();
    }

    public static BlockObject getBlock(String id) {
        return registry.get(id);
    }

    public static BlockObject getBlock(Block block) {
        return getBlock(block.getDescriptionId().replace("block.minecraft.", ""));
    }

}

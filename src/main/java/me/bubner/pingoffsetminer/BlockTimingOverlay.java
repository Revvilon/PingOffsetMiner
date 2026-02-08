package me.bubner.pingoffsetminer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.bubner.pingoffsetminer.util.MiningSpeedCalculator;
import me.bubner.pingoffsetminer.util.Util;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.OptionalDouble;

import static net.minecraft.client.renderer.RenderStateShard.ITEM_ENTITY_TARGET;
import static net.minecraft.client.renderer.RenderStateShard.VIEW_OFFSET_Z_LAYERING;

public class BlockTimingOverlay {
    public static RenderType THICK_LINES = RenderType.create(
            "pom_lines",
            1536,
            RenderPipelines.LINES,
            RenderType.CompositeState.builder()
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(5)))
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .createCompositeState(false)
    );
    private BlockPos currentBlock;
    private double ticksNeeded;
    private boolean timeoutExceeded;
    private int startServerTick;

    public BlockTimingOverlay(ModConfig config) {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, outlineRenderState) -> {
            Minecraft mc = Minecraft.getInstance();
            HitResult hr = mc.hitResult;
            if (hr instanceof BlockHitResult) {
                BlockPos blockPos = ((BlockHitResult) hr).getBlockPos();
                if (!config.getActive() || !Util.isInSkyblock()) {
                    ticksNeeded = -1;
                    return true;
                }

                if (!blockPos.equals(currentBlock) || !mc.options.keyAttack.isDown()) {
                    timeoutExceeded = false;
                    currentBlock = blockPos;
                    // use player tick counter as approximation of server tick start
                    if (mc.player != null) startServerTick = mc.player.tickCount;
                }
                if (currentBlock == null || mc.level == null) return true;

                BlockState bs = mc.level.getBlockState(currentBlock);
                String blockName = MiningSpeedCalculator.getBlockName(bs.getBlock());
                double ticks = MiningSpeedCalculator.getTicksToBreak(
                        MiningSpeedCalculator.blockHardnesses.getOrDefault(blockName, -1),
                        config.getMiningSpeed()
                );
                if (ticks == -1) return true;
                ticksNeeded = ticks;

                if (mc.level == null || context.consumers() == null) return true;

                Camera camera = mc.getEntityRenderDispatcher().camera;
                if (camera == null) return true;
                Vec3 camPos = camera.getPosition();
                VoxelShape shape = bs.getShape(mc.level, currentBlock);

                PoseStack poseStack = context.matrices();
                VertexConsumer buffer = context.consumers().getBuffer(THICK_LINES);

                float red = timeoutExceeded ? 0f : 1f;
                float green = timeoutExceeded ? 1f : 0f;

                double dx = currentBlock.getX() - camPos.x;
                double dy = currentBlock.getY() - camPos.y;
                double dz = currentBlock.getZ() - camPos.z;

                Matrix4f matrix = poseStack.last().pose();
                shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
                    Vector3f dir = new Vector3f((float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1))
                            .normalize();
                    buffer.addVertex(matrix, (float) (x1 + dx), (float) (y1 + dy), (float) (z1 + dz))
                            .setColor(red, green, 0, 1)
                            .setNormal(dir.x, dir.y, dir.z);
                    buffer.addVertex(matrix, (float) (x2 + dx), (float) (y2 + dy), (float) (z2 + dz))
                            .setColor(red, green, 0, 1)
                            .setNormal(dir.x, dir.y, dir.z);
                });
            }
            // Use our outline
            return false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            int ticksElapsed = client.player.tickCount - startServerTick;
            // TODO: Dynamic ping calculation?
            double pingSec = config.getPing() / 1000.0;
            double pingOffset = pingSec > 0 && ticksNeeded > 0
                    ? ticksNeeded - pingSec * 20.0
                    : ticksNeeded;
            timeoutExceeded = ticksNeeded > 0 && ticksElapsed >= pingOffset;
        });
    }
}

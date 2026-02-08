package me.bubner.pingoffsetminer;

import me.bubner.pingoffsetminer.util.MiningSpeedCalculator;
import me.bubner.pingoffsetminer.util.Util;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockTimingOverlay {
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

                // TODO: test world
                PingOffsetMiner.LOGGER.info(blockName);
                // Using original GL calls from 1.8.9 for simplicity TODO
//                glPushMatrix();
//                glEnable(GL_BLEND);
//                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//                glEnable(GL_LINE_SMOOTH);
//                glLineWidth(4);
//                glDisable(GL_TEXTURE_2D);
//                glEnable(GL_CULL_FACE);
//                glDisable(GL_DEPTH_TEST);
//
//                EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
//                Camera camera = erd.camera;
//                if (camera == null) return true;
//                Vec3 camPos = camera.getPosition();
//                glTranslated(-camPos.x, -camPos.y, -camPos.z);
//                glTranslated(blockPos.getX(), blockPos.getY(), blockPos.getZ());
//                glColor4f(timeoutExceeded ? 0f : 1f, timeoutExceeded ? 1f : 0f, 0f, 1f);
//                Util.drawBox();
//                glColor4f(1f, 1f, 1f, 1f);
//                glEnable(GL_DEPTH_TEST);
//                glEnable(GL_TEXTURE_2D);
//                glDisable(GL_BLEND);
//                glDisable(GL_LINE_SMOOTH);
//                glPopMatrix();
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

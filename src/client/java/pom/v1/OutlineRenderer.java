package pom.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import pom.v1.modmenu.pomConfig;

import java.awt.*;
import java.util.HashMap;


public class OutlineRenderer implements ClientModInitializer {


    private BlockPos currentBlock;
    private double ticksNeeded;
    private boolean timeoutExceeded;
    private int startServerTick;

    public long lastSent = 0L;

    pomConfig Config = pomConfig.HANDLER.instance();

    @Override
    public void onInitializeClient() {
        PingOffsetMinerClient.LOGGER.info("Initialized!");


        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if ((System.currentTimeMillis() - lastSent) >= 10000) {
                lastSent = System.currentTimeMillis();
                if (Config.debug && !Config.active && !Util.getIsland()) return ActionResult.PASS;
                if (Util.speed() == -1 && !Config.debug && Config.active && Util.getIsland()) {
                    Util.sendMsg(Text.literal("Mining Speed not found! Please enable in tab widget"));
                    Util.sendMsg(Text.literal("To enable: /tab -> Stats Widget -> Shown Stats -> Mining Speed"));
                }
                if (Util.tps <= 0) {
                    Util.sendMsg(Text.literal("Tps not found! Please wait a little bit"));
                }

                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, outline) -> {
            long time = System.currentTimeMillis();
            if (!Util.getIsland() && !Config.debug) {
                log("Island not found!", time);
                return true;
            };
            MinecraftClient mc = MinecraftClient.getInstance();
            HitResult hr = mc.crosshairTarget;

            if (hr instanceof BlockHitResult) {
                BlockPos blockPos = ((BlockHitResult) hr).getBlockPos();
                if (!Config.active) {
                    ticksNeeded = -1;
                    log("Not active!", time);
                    return true;
                }

                if (!blockPos.equals(currentBlock) || !mc.options.attackKey.isPressed()) {
                    timeoutExceeded = false;
                    ticksNeeded = -1;
                    currentBlock = blockPos;

                    if (mc.player != null) startServerTick = mc.player.age;
                }

                if (currentBlock == null || mc.world == null || worldRenderContext.consumers() == null) {
                    log("Block, World or RenderContext is null!", time);
                    return true;
                };

                BlockState bs = mc.world.getBlockState(currentBlock);
                String blockName = SpeedCalc.getBlockName(bs.getBlock(), blockPos);
                double extra = blockName.contains("gem") && Config.extra ? 855 : 0;
                double ticks = SpeedCalc.getTicksToBreak(
                        SpeedCalc.blockHardness.getOrDefault(blockName, -1),
                        (Util.speed() + extra)

                );

                if (!Config.blockEnabled.getOrDefault(blockName, false)) {
                    log("Block not enabled!", time);
                    return true;
                }

                if (ticks == -1) {
                    log("Ticks needed is null!", time);
                    return true;
                };
                ticksNeeded = ticks;

                Camera camera = mc.getEntityRenderDispatcher().camera;
                if (camera == null) {
                    log("Camera is null!", time);
                    return true;
                }
                Vec3d camPos = camera.getPos();
                VoxelShape shape = bs.getOutlineShape(mc.world, currentBlock);

                MatrixStack matrixStack = worldRenderContext.matrices();
                VertexConsumer buffer = worldRenderContext.consumers().getBuffer(Config.selectedLine.getLayer());
                Color red = timeoutExceeded ? Config.color2 : Config.color1;

                double dx = currentBlock.getX() - camPos.x;
                double dy = currentBlock.getY() - camPos.y;
                double dz = currentBlock.getZ() - camPos.z;


                Matrix4f matrix = matrixStack.peek().getPositionMatrix();

                shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    Vector3f dir = new Vector3f((float) (maxX - minX), (float) (maxY - minY), (float) (maxZ - minZ))
                            .normalize();
                    buffer.vertex(matrix, (float) (minX + dx), (float) (minY + dy), (float) (minZ + dz))
                            .color(red.getRed(), red.getGreen(), red.getBlue(), red.getAlpha())
                            .normal(dir.x, dir.y, dir.z);
                    buffer.vertex(matrix, (float) (maxX + dx), (float) (maxY + dy), (float) (maxZ + dz))
                            .color(red.getRed(), red.getGreen(), red.getBlue(), red.getAlpha())
                            .normal(dir.x, dir.y, dir.z);
                });
            }
            log("Rendering outlines now!", time);
            return false;

        });
        ClientTickEvents.END_CLIENT_TICK.register(clientWorld -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null && client.world != null) {
                int ticksElapsed = client.player.age - startServerTick;

                double debugTps = Config.debug ? 20 : Util.tps;
                double pingSec = Config.debug ? Config.ping / 1000.0 : Util.getAverage(10) / 1000.0;

                double pingOffset = pingSec > 0 && ticksNeeded > 0
                        ? ticksNeeded - pingSec * debugTps
                        : ticksNeeded;
                timeoutExceeded = ticksNeeded > 0 && ticksElapsed >= pingOffset && client.options.attackKey.isPressed();
            }
            if (sound && timeoutExceeded && Config.sound && client.options.attackKey.isPressed()) {
                sound = false;
                SoundEvent useSound = SoundEvent.of(Identifier.of(Config.soundpath));
                if (client.player == null) return;
                client.player.playSound(useSound);
            }
            if (!sound && !timeoutExceeded) sound = true;
        });
    }

    public boolean sound = false;

    HashMap<String, Long> logs = new HashMap<String, Long>();

    private void log(String text, Long time) {
        if (!Config.logging) return;
        logs.putIfAbsent(text, 10001L);
        if ((System.currentTimeMillis() - logs.get(text)) <= 10000) return;
        Util.sendMsg(Text.literal(text).formatted(Formatting.RED, Formatting.BOLD));
        logs.replace(text, time);
    }
}

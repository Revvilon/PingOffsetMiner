package pom.v1;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;


public class OutlineRenderer implements ClientModInitializer {

    private static final RenderLayer THICK_LINES = RenderLayer.SECONDARY_BLOCK_OUTLINE;

    private BlockPos currentBlock;
    private double ticksNeeded;
    private boolean timeoutExceeded;
    private int startServerTick;

    public long time;

    @Override
    public void onInitializeClient() {

        PingOffsetMinerClient.LOGGER.info("Initialized!");

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (speed() == -1 && (System.currentTimeMillis() - time) >=  5*1000) {

                time = System.currentTimeMillis();

                Util.sendMsg("Mining Speed not found! Please enable in tab widget", Formatting.RED);
                Util.sendMsg("To enable: /tab -> Stats Widget -> Shown Stats -> Mining Speed", Formatting.RED);
                Util.sendMsg("This setting must be enabled on all islands", Formatting.RED);



                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });


            WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(((worldRenderContext, outlineRenderState) -> {

                MinecraftClient mc = MinecraftClient.getInstance();
                HitResult hr = mc.crosshairTarget;

                if (!Config.getActive()) return true;

                if (hr instanceof BlockHitResult) {
                    BlockPos blockPos = ((BlockHitResult) hr).getBlockPos();
                    if (!blockPos.equals(currentBlock) || !mc.mouse.wasLeftButtonClicked()) {
                        timeoutExceeded = false;
                        currentBlock = blockPos;
                        if (mc.player != null) startServerTick = mc.player.age;
                    }

                    if (currentBlock == null || mc.world == null) {
                        return true;
                    }



                    BlockState bs = mc.world.getBlockState(currentBlock);
                    String blockName = SpeedCalc.getBlockName(bs.getBlock(), blockPos);
                    double ticks = SpeedCalc.getTicksToBreak(
                            SpeedCalc.blockHardness.getOrDefault(blockName, -1),
                            speed()

                    );

                    if (ticks == -1) {
                        return true;
                    }
                    ticksNeeded = ticks;

                    if (mc.world == null || worldRenderContext.consumers() == null) {
                        return true;
                    }

                    Camera camera = mc.getEntityRenderDispatcher().camera;
                    if (camera == null) {
                        return true;
                    }
                    Vec3d camPos = camera.getPos();
                    VoxelShape shape = bs.getOutlineShape(mc.world, currentBlock);

                    MatrixStack matrixStack = worldRenderContext.matrices();
                    VertexConsumer buffer = worldRenderContext.consumers().getBuffer(THICK_LINES);

                    float red = timeoutExceeded ? 0f : 1f;
                    float green = timeoutExceeded ? 1f : 0f;

                    double dx = currentBlock.getX() - camPos.x;
                    double dy = currentBlock.getY() - camPos.y;
                    double dz = currentBlock.getZ() - camPos.z;

                    Matrix4f matrix = matrixStack.peek().getPositionMatrix();
                    shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                        Vector3f dir = new Vector3f((float) (maxX - minX), (float) (maxY - minY), (float) (maxZ - minZ))
                                .normalize();
                        buffer.vertex(matrix, (float) (minX + dx), (float) (minY + dy), (float) (minZ + dz))
                                .color(red, green, 0, 1)
                                .normal(dir.x, dir.y, dir.z);
                        buffer.vertex(matrix, (float) (maxX + dx), (float) (maxY + dy), (float) (maxZ + dz))
                                .color(red, green, 0, 1)
                                .normal(dir.x, dir.y, dir.z);
                    });
                }
                return false;
            }));


        ClientTickEvents.END_WORLD_TICK.register(minecraftServer -> {
            MinecraftClient client = MinecraftClient.getInstance();


            if (client.player != null) {

                    int ticksElapsed = client.player.age - startServerTick;

                    double pingSec = (double) PingOffsetMinerClient.getAveragePing(10) / 1000.0;
                    double pingOffset = pingSec > 0 && ticksNeeded > 0
                            ? ticksNeeded - pingSec * 20.0
                            : ticksNeeded;
                    timeoutExceeded = ticksNeeded > 0 && ticksElapsed >= pingOffset;

            }
        });

    }

    public double speed() {
        var network = MinecraftClient.getInstance().getNetworkHandler();
        if (network == null)  {
            return -1;
        }


        for (PlayerListEntry entry : network.getPlayerList()) {
            if (entry.getDisplayName() == null) continue;

            String text = entry.getDisplayName().getString();

            if (text.contains("Mining Speed:") || text.contains("⸕")) {
                String cleaned = text.replaceAll("[^0-9]", "").replace("⸕", "");
                if (!cleaned.isEmpty()) {
                    return Double.parseDouble(cleaned);
                }
            }
        }
        return -1;

    }
}

package pom.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import pom.v1.modmenu.pomConfig;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

import static pom.v1.render.pomRender.filledBoxDraw;


public class OutlineRenderer implements ClientModInitializer {

    // Initialize variables
    private BlockPos currentBlock;
    private BlockState bs;
    private double ticksNeeded = -1;
    private boolean timeoutExceeded;
    private int startServerTick;
    private Vec3 camPos;
    private VoxelShape shape;
    private String blockName;

    public long lastSent = 0L;

    // Config handler
    pomConfig Config = pomConfig.HANDLER.instance();

    boolean sound = false;

    HashMap<String, Long> logs = new HashMap<String, Long>();



    private void log(String text, Long time) {
        if (!Config.logging) return;
        logs.putIfAbsent(text, 10001L);
        if ((System.currentTimeMillis() - logs.get(text)) <= 2000) return;
        Util.sendMsg(Component.literal(text).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        logs.replace(text, time);
    }


    @Override
    public void onInitializeClient() {
        PingOffsetMinerClient.LOGGER.info("Initialized!");

        // Send message to player
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if ((System.currentTimeMillis() - lastSent) >= 10000) {
                lastSent = System.currentTimeMillis();
                if (Config.debug && !Config.active && !Util.getIsland()) return InteractionResult.PASS;
                if (Util.speed() == -1 && !Config.debug && Config.active && Util.getIsland()) {
                    Util.sendMsg(Component.literal("Mining Speed not found! Please enable in tab widget"));
                    Util.sendMsg(Component.literal("To enable: /tab -> Stats Widget -> Shown Stats -> Mining Speed"));
                    Util.sendMsg(Component.literal("Please make sure that the Stats Widget is visible in the tab-list."));
                }
                if (Util.tps <= 0) {
                    Util.sendMsg(Component.literal("Tps not found! Please wait a little bit"));
                }

                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {

            long time = System.currentTimeMillis();
            Minecraft mc = Minecraft.getInstance();
            if (client.level == null || client.player == null || mc.level == null) return;

            BlockPos blockPos = Util.blockPos(client);
            // Reset if not currently looking at block
            if (!Objects.equals(blockPos, currentBlock) || !client.options.keyAttack.isDown()) {
                ticksNeeded = -1;
                sound = false;
                currentBlock = blockPos;
                startServerTick = client.player.tickCount;
            }
            if (!Config.active) {
                ticksNeeded = -1;
                log("Not active!", time);
                return;
            }

            // Get island
            if (!Util.getIsland() && !Config.debug) {
                log("Island not found!", time);
                return;
            }

            if (currentBlock != null) {
                bs = mc.level.getBlockState(currentBlock);
                blockName = SpeedCalc.getBlockName(bs.getBlock());

                // Calculate mining speed
                double extra = blockName.contains("gem") && Config.extra ? Config.extraVal : 0;
                double ticks = SpeedCalc.getTicksToBreak(
                        SpeedCalc.blockHardness.getOrDefault(blockName, -1),
                        (Util.speed() + extra)

                );

                if (!Config.blockEnabled.getOrDefault(blockName, false)) {

                    currentBlock = null;
                    log("Block not enabled!", time);
                    return;
                }

                if (ticks == -1) {
                    log("Ticks needed is null!", time);
                    return;
                }
                shape = bs.getShape(mc.level, currentBlock);

                ticksNeeded = ticks;

                if (Util.boost()) return;

                if (client.player != null && client.level != null) {
                    int ticksElapsed = client.player.tickCount - startServerTick;

                    double debugTps = Config.debug ? 20 : Util.tps;
                    double pingSec = Config.debug ? Config.ping / 1000.0 : Util.getAverage(10) / 1000.0;

                    double pingOffset = pingSec > 0 && ticksNeeded > 0
                            ? ticksNeeded - pingSec * debugTps
                            : ticksNeeded;
                    timeoutExceeded = ticksNeeded > 0 && ticksElapsed >= pingOffset && client.options.keyAttack.isDown();
                }
                log("Ticks needed: " + ticksNeeded, System.currentTimeMillis());
                log("TPS: " + Util.tps, System.currentTimeMillis());
                log("Mining speed: " + Util.speed(), System.currentTimeMillis());

                if (sound && timeoutExceeded && Config.sound && client.options.keyAttack.isDown()) {
                    sound = false;
                    if (client.player == null) return;
                    SoundEvent useSound = SoundEvent.createVariableRangeEvent(Identifier.parse(Config.soundpath));
                    client.player.playSound(useSound);
                }
                if (!sound && !timeoutExceeded) sound = true;
            };
        });
        WorldRenderEvents.BEFORE_TRANSLUCENT.register((context) -> {
            if (Config.active && shape != null && currentBlock != null && !Util.boost()) {
                PoseStack stack = context.matrices();
                Color blockCol = timeoutExceeded ? Config.blockCol2 : Config.blockCol1;

                stack.pushPose();

                filledBoxDraw(
                        stack, context, shape, blockCol, currentBlock
                );

                stack.popPose();
            }
        });

/*
WorldRenderEvents.BEFORE_TRANSLUCENT.register(context -> {
                    VoxelShape shape = bs.getShape(Minecraft.getInstance().level, currentBlock);

                    PoseStack matrixStack = context.matrices();
                    VertexConsumer buffer = context.consumers().getBuffer(());
                    Color red = timeoutExceeded ? Config.color2 : Config.color1;

                    double dx = currentBlock.getX() - camPos.x;
                    double dy = currentBlock.getY() - camPos.y;
                    double dz = currentBlock.getZ() - camPos.z;

                    Matrix4f matrix = matrixStack.peek().getPositionMatrix();
                    VertexRendering.drawOutline(
                            ,
                            buffer,
                            shape,
                            currentBlock.getX(), currentBlock.getY(), currentBlock.getZ(),
                            red.getRGB()
                    );

                    Color blockCol = timeoutExceeded ? Config.blockCol2 : Config.blockCol1;

                    VoxelShape boxShape = bs.getOutlineShape(mc.world, blockPos)
                            .offset(-camPos.x, -camPos.y, -camPos.z);
                    VertexConsumer boxBuf = worldRenderContext.consumers().getBuffer(DEBUG_FILLED_BOX_TEST_PHASE);
                    boxShape.forEachBox(((minX, minY, minZ, maxX, maxY, maxZ) -> {
                        myDrawFilledBox(
                                matrixStack,
                                boxBuf,
                                (float) minX + blockPos.getX(), (float) minY + blockPos.getY(), (float) minZ + blockPos.getZ(),
                                (float) maxX + blockPos.getX(), (float) maxY + blockPos.getY(), (float) maxZ + blockPos.getZ(),
                                (float) (blockCol.getRed() / 255.0), (float) (blockCol.getGreen() / 255.0), (float) (blockCol.getBlue() / 255.0), (float) (blockCol.getAlpha() / 255.0)
                        );
                    }));
                }

                log("Mining speed: " + Util.speed(), time);
        log("Rendering outlines now!", time);

        return;
        });

    public void drawOutline(WorldRenderContext context, BlockPos pos, VoxelShape shape, Color color) {
        Vec3d cam = context.worldState().cameraRenderState.pos;
        context.matrices().push();
        context.matrices().translate(pos.getX() - cam.x, pos.getY() -cam.y, pos.getZ() - cam.z);

        VertexConsumer buffer = context.consumers().getBuffer(DEBUG_OUTLINE_PHASE);

        VertexRendering.drawOutline(
                context.matrices(),
                buffer,
                shape,
                0, 0, 0,
                color.getRGB()
        );
    }*/

}}

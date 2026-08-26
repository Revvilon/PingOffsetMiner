package pom.rewrite.utility.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import pom.rewrite.features.render.HighlightRender;
import pom.rewrite.features.render.OutlineRender;
import pom.rewrite.utility.block.BlockData;
import pom.rewrite.utility.block.BlockObject;
import pom.rewrite.utility.block.BlockUtil;
import pom.rewrite.utility.server.IslandUtils;
import pom.rewrite.utility.stats.MiningStats;
import pom.rewrite.utility.stats.TickStats;


public class RenderClass {

    private static final TickStats ticks = TickStats.instance();
    private static final MiningStats miningStats = MiningStats.instance();
    private static final BlockData blockData = BlockData.getInstance();

    public static void init() {
        LevelRenderEvents.END_MAIN.register(context -> {
            if (miningStats.shouldRender() && IslandUtils.isIsland()) {
                BlockObject blockObject = blockData.getCurrentBlock();
                if (blockObject == null) return;
                VoxelShape voxelShape = BlockUtil.getShape(blockObject.state, blockObject.pos);

                Color highlightColor = ticks.timeoutExceeded() ? HighlightRender.postMined.getColor() : HighlightRender.preMined.getColor();
                RenderPipeline highlightPipeline = CustomPipelines.getHighlight(HighlightRender.depthToggle.getBool());

                Color outlineColor = ticks.timeoutExceeded() ? OutlineRender.postMined.getColor() : OutlineRender.preMined.getColor();
                double lineWidth = OutlineRender.lineWidth.getDouble();
                RenderPipeline outlinePipeline = CustomPipelines.getOutline(OutlineRender.depthToggle.getBool());

                // Render Highlight
                if (HighlightRender.instance.isEnabled()) extractAndDraw(context, voxelShape, blockObject.pos, highlightColor, highlightPipeline);
                // Render Outline
                if (OutlineRender.instance.isEnabled()) extractAndDraw(context, voxelShape, blockObject.pos, outlineColor, lineWidth, outlinePipeline);
            }
        });

        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((context, state) -> !(miningStats.shouldRender() && IslandUtils.isIsland()));
    }

    private static void extractAndDraw(LevelRenderContext context, VoxelShape shape, BlockPos pos, Color color, RenderPipeline renderPipeline) {
        //? if >=26.2 {
        RenderUtil.instance.renderHighlight(context, shape, pos, color, renderPipeline);
        //?}
        //? if <=26.1.2 {
            //RenderUtil.instance.renderHighlight(context, shape, pos, color, renderPipeline);
            //RenderUtil.instance.draw(Minecraft.getInstance(), renderPipeline);
        //? }
    }

    private static void extractAndDraw(LevelRenderContext context, VoxelShape shape, BlockPos pos, Color color, double lineWidth, RenderPipeline renderPipeline) {
        //? if >= 26.2 {
        RenderUtil.instance.renderOutline(context, shape, pos, color, lineWidth, renderPipeline);
        //? }
        //? if <= 26.1.2 {
        //RenderUtil.instance.renderOutline(context, shape, pos, color, lineWidth, renderPipeline);
        //RenderUtil.instance.draw(Minecraft.getInstance(), renderPipeline);
        //? }
    }
}

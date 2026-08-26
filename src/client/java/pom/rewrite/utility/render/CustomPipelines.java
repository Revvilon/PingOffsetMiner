package pom.rewrite.utility.render;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Optional;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class CustomPipelines {

    //? if >=26.2 {
    private static final CompareOp DEPTH_FUNC = CompareOp.GREATER_THAN_OR_EQUAL;
    private static final float DEPTH_SCALE = 1.0F;
    //?} else {
    /*
    private static final CompareOp DEPTH_FUNC = CompareOp.LESS_THAN_OR_EQUAL;
    private static final float DEPTH_SCALE = -1.0F;
    */
    //?}

    // Custom pipeline highlight
    private static final RenderPipeline highlight_depth = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_filled_box_depth"))
            .withDepthStencilState(new DepthStencilState(DEPTH_FUNC, true, DEPTH_SCALE, DEPTH_SCALE))
            .withCull(true)
            .build());

    private static final RenderPipeline highlight_no_depth = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_filled_box_no_depth"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build());

    // Custom pipeline lines
    private static final RenderPipeline lines_depth = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_lines_render_depth"))
                    .withDepthStencilState(new DepthStencilState(DEPTH_FUNC, true, DEPTH_SCALE, DEPTH_SCALE))
                    .withCull(false)
                    .build()
    );

    private static final RenderPipeline lines_no_depth = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_lines_render_no_depth"))
                    .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                    .withCull(false)
                    .build()
    );

    public static RenderPipeline getHighlight(boolean depth) {
        return depth ? highlight_depth : highlight_no_depth;
    }

    public static RenderPipeline getOutline(boolean depth) {
        return depth ? lines_depth : lines_no_depth;
    }
}

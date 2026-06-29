package pom.rewrite.utility.render;

import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class CustomPipelines {
    // Custom pipeline highlight
    private static final RenderPipeline highlight_depth = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_filled_box"))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true, -1.0f, -1.0f))
            .withCull(true)
            .build());

    private static final RenderPipeline highlight_no_depth = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_filled_box"))
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .build());

    // Custom pipeline lines
    private static final RenderPipeline lines_depth = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_lines_render"))
                    .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true, -1.0f, -1.0f))
                    .withCull(false)
                    .build()
    );

    private static final RenderPipeline lines_no_depth = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_lines_render"))
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

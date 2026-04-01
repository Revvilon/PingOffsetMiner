package pom.v1.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import static pom.v1.PingOffsetMinerClient.MOD_ID;
import static pom.v1.PomConfig.PomConfig.Config;

public class PomPipelines {

    public static RenderPipeline getHighlight() {
        return Config().highlight.depth.get() ? highlight_depth : highlight_no_depth;
    }

    public static RenderPipeline getLine() {
        return Config().line.depth.get() ? lines_depth : lines_no_depth;
    }
    // Custom pipeline highlight
    private static RenderPipeline highlight_depth = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_filled_box"))
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(true)
            .withDepthBias(-0.1f, -0.1f)
            .withCull(true)
            .build());

    private static RenderPipeline highlight_no_depth = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_filled_box"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build());

    // Custom pipeline lines
    private static RenderPipeline lines_depth = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_lines_render"))
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withCull(false)
                    .withDepthWrite(true)
                    .withDepthBias(-1f, -1f)
                    .build()
    );

    private static RenderPipeline lines_no_depth = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/debug_lines_render"))
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withCull(false)
                    .build()
    );
}

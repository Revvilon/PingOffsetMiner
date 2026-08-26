package pom.rewrite.utility.render;


import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

//? if >= 26.2 {
import com.mojang.blaze3d.PrimitiveTopology;
import net.minecraft.client.renderer.StagedVertexBuffer;
import oshi.jna.platform.mac.SystemB;
import pom.rewrite.features.render.HighlightRender;
import pom.rewrite.features.render.OutlineRender;
//?}


import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class RenderUtil {
    public static final RenderUtil instance = new RenderUtil();

    private static final ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private BufferBuilder buffer;


    private static final Vector4f COLOR_MODULATOR = new Vector4f(1, 1, 1, 1);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private MappableRingBuffer vertexBuffer;

    //? if >= 26.2 {

    private record LineState(VoxelShape shape, BlockPos pos, Color color, float width) { }
    private LineState renderState;
    private record HighlightState(VoxelShape shape, BlockPos pos, Color color) { }
    private HighlightState highlightState;
    private final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "pom buffer", RenderType.SMALL_BUFFER_SIZE);


    void renderOutline(LevelRenderContext context, VoxelShape shape, BlockPos pos, io.wispforest.owo.ui.core.Color color, double lineWidth, RenderPipeline pipeline) {
        this.renderState = new LineState(shape, pos, color, (float) lineWidth);

        VertexFormat formatBinding = pipeline.getVertexFormatBinding(0);
        assert formatBinding != null;

        PrimitiveTopology primitive = pipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(
                formatBinding,
                primitive,
                primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null);

        this.render(context, draw);

        stagedBuffer.upload();

        StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);

        if (info != null) {
            draw(Minecraft.getInstance(), info, pipeline);
        }

        stagedBuffer.endFrame();
    }

    public void renderHighlight(LevelRenderContext context, VoxelShape shape, BlockPos pos, Color color, RenderPipeline pipeline) {
        this.highlightState = new HighlightState(shape, pos, color);
        VertexFormat formatBinding = pipeline.getVertexFormatBinding(0);
        assert formatBinding != null;

        PrimitiveTopology primitive = pipeline.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = stagedBuffer.appendDraw(
                formatBinding,
                primitive,
                primitive == PrimitiveTopology.QUADS ? RenderSystem.getProjectionType().vertexSorting() : null
        );

        this.renderBox(context, draw);

        stagedBuffer.upload();

        StagedVertexBuffer.ExecuteInfo info = stagedBuffer.getExecuteInfo(draw);

        if (info != null) {
            draw(Minecraft.getInstance(), info, pipeline);
        }

        stagedBuffer.endFrame();
    }

    void renderBox(LevelRenderContext context, StagedVertexBuffer.Draw draw) {
        BlockPos pos = this.highlightState.pos;
        Color color = this.highlightState.color;

        Vec3 camera = context.levelState().cameraRenderState.pos;

        float originX = (float) (pos.getX() - camera.x);
        float originY = (float) (pos.getY() - camera.y);
        float originZ = (float) (pos.getZ() - camera.z);

        PoseStack matrices = context.poseStack();
        matrices.pushPose();
        matrices.translate(originX, originY, originZ);

        final var builder = stagedBuffer.getVertexBuilder(draw);

        this.highlightState.shape.forAllBoxes((minX,  minY, minZ, maxX, maxY, maxZ) -> renderFilledBox(
                matrices.last().pose(),
                builder,
                (float) minX, (float) minY, (float) minZ,
                (float) maxX, (float) maxY, (float) maxZ,
                color.red(), color.green(),  color.blue(),color.alpha()
        ));

        matrices.popPose();
    }

    void render(LevelRenderContext context, StagedVertexBuffer.Draw draw) {
        BlockPos pos = this.renderState.pos;

        Vec3 camera = context.levelState().cameraRenderState.pos;

        float originX = (float) (pos.getX() - camera.x);
        float originY = (float) (pos.getY() - camera.y);
        float originZ = (float) (pos.getZ() - camera.z);

        PoseStack matrices = context.poseStack();
        matrices.pushPose();
        matrices.translate(originX, originY, originZ);

        final var builder = stagedBuffer.getVertexBuilder(draw);

        this.renderState.shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
           Vector3f start = new Vector3f((float) minX, (float) minY, (float) minZ);
           Vector3f end = new Vector3f((float) maxX, (float) maxY, (float) maxZ);
            renderLine(builder, matrices.last().pose(), start, end, this.renderState.color, (float) (this.renderState.width / 100));

        });
    }

    private void draw(Minecraft client, StagedVertexBuffer.ExecuteInfo info, RenderPipeline pipeline) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        RenderTarget mainTarget = client.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = mainTarget.getColorTextureView();

        assert colorTexture != null;

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() ->
                        MOD_ID + "pom rendering",
                        colorTexture,
                        Optional.empty(),
                        mainTarget.getDepthTextureView(),
                        OptionalDouble.empty()
                )) {
            renderPass.setPipeline(pipeline);

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);

            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());

            renderPass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
        }
    }

    public void close() {
        stagedBuffer.close();
    }

    //? } else {
    /* void renderOutline(LevelRenderContext context, VoxelShape shape, BlockPos pos, io.wispforest.owo.ui.core.Color color, double lineWidth, RenderPipeline renderPipeline) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        matrices.pushPose();

        double x = pos.getX() - camera.x;
        double y = pos.getY() - camera.y;
        double z = pos.getZ() - camera.z;
        matrices.translate((float) x, (float) y, (float) z);

        if (buffer == null) {


            buffer = new BufferBuilder(allocator, renderPipeline.getVertexFormatMode(), renderPipeline.getVertexFormat());
        }

        Matrix4f matrix = matrices.last().pose();

        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {

            Vector3f start = new Vector3f((float)minX, (float)minY, (float)minZ);
            Vector3f end = new Vector3f((float)maxX, (float)maxY, (float)maxZ);

            renderLine(buffer, matrix, start,  end, color, (float) (lineWidth / 100));

        });

        matrices.popPose();
    }

      void renderHighlight(LevelRenderContext context, VoxelShape shape, BlockPos pos, Color color, RenderPipeline renderPipeline) {
        PoseStack matrices = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        matrices.pushPose();

        float x = (float) (pos.getX() - camera.x);
        float y = (float) (pos.getY() - camera.y);
        float z = (float) (pos.getZ() - camera.z);

        matrices.translate(x, y, z);

        if (buffer == null) {

            buffer = new BufferBuilder(allocator, renderPipeline.getVertexFormatMode(), renderPipeline.getVertexFormat());
        }

        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> renderFilledBox(
                matrices.last().pose(),
                buffer,
                (float) minX, (float) minY, (float) minZ,
                (float) maxX, (float) maxY, (float) maxZ,
                color.red(), color.green(),  color.blue(),color.alpha()
        ));
        matrices.popPose();
    }

    void draw(Minecraft client, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline) {

        MeshData builtBuffer = buffer.buildOrThrow();
        MeshData.DrawState drawParameters = builtBuffer.drawState();
        VertexFormat format = drawParameters.format();

        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);

        draw(client, pipeline, builtBuffer, drawParameters, vertices, format);

        vertexBuffer.rotate();
        buffer = null;
    }

    private GpuBuffer upload(MeshData.DrawState drawParameters, VertexFormat format, MeshData builtBuffer) {
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();

        if (vertexBuffer == null || vertexBuffer.size() < vertexBufferSize) {
            if (vertexBuffer != null) {
                vertexBuffer.close();
            }

            vertexBuffer = new MappableRingBuffer(() -> MOD_ID + " render pipeline", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, vertexBufferSize);
        }

        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();

        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(vertexBuffer.currentBuffer().slice(0, builtBuffer.vertexBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(builtBuffer.vertexBuffer(), mappedView.data());
        }

        return vertexBuffer.currentBuffer();
    }

    private static void draw(Minecraft client, RenderPipeline pipeline, MeshData builtBuffer, MeshData.DrawState drawParameters, GpuBuffer vertices, VertexFormat format) {
        GpuBuffer indices;
        VertexFormat.IndexType indexType;

        if (pipeline.getVertexFormatMode() == VertexFormat.Mode.QUADS) {
            builtBuffer.sortQuads(allocator, RenderSystem.getProjectionType().vertexSorting());


            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.indexBuffer());
            indexType = builtBuffer.drawState().indexType();
        } else {
            RenderSystem.AutoStorageIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.type();
        }

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);

        var depthView = client.getMainRenderTarget().getDepthTextureView();

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> MOD_ID + "pom pipeline renderer",
                        client.getMainRenderTarget().getColorTextureView(),
                        OptionalInt.empty(),
                        depthView,
                        java.util.OptionalDouble.empty()
                )) {
            renderPass.setPipeline(pipeline);

            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);


            renderPass.setVertexBuffer(0, vertices);
            renderPass.setIndexBuffer(indices, indexType);

            renderPass.drawIndexed(0, 0, drawParameters.indexCount(), 1);
        }
        builtBuffer.close();

    }

     public void close() {
        allocator.close();

        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
    }

    */
    //? }






    private void renderLine(VertexConsumer buffer, Matrix4f matrix, Vector3f start, Vector3f end, io.wispforest.owo.ui.core.Color color, float width) {

        //~ if < 26.2 'mainCamera()' -> 'getMainCamera()'
        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();

        Vector3f lineDir = new Vector3f(end).sub(start).normalize();

        Vector3f toCamera = new Vector3f(0, 0, 1).rotate(camera.rotation());

        Vector3f side = new Vector3f(lineDir).cross(toCamera);

        if (side.lengthSquared() < 0.001f) {
            Vector3f cameraUp = new Vector3f(0, 1, 0).rotate(camera.rotation());
            side = new Vector3f(lineDir).cross(cameraUp);
        }

        side.normalize().mul(width / 2f);

        float r = color.red();
        float g = color.green();
        float b = color.blue();
        float a = color.alpha();

        buffer.addVertex(matrix, start.x() + side.x(), start.y() + side.y(), start.z() + side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, start.x() - side.x(), start.y() - side.y(), start.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, end.x() - side.x(), end.y() - side.y(), end.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, end.x() + side.x(), end.y() + side.y(), end.z() + side.z()).setColor(r, g, b, a);

        buffer.addVertex(matrix, end.x() + side.x(), end.y() + side.y(), end.z() + side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, end.x() - side.x(), end.y() - side.y(), end.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, start.x() - side.x(), start.y() - side.y(), start.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, start.x() + side.x(), start.y() + side.y(), start.z() + side.z()).setColor(r, g, b, a);
    }
    private void renderFilledBox(Matrix4fc positionMatrix, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
        // Front Face
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Back face
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // Left face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Right face
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);

        // Top face
        buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // Bottom face
        buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
    }
}
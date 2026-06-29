package pom.v1.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import pom.v1.PomConfig.PomConfig;

import java.awt.*;
import java.util.OptionalInt;

import static pom.v1.PingOffsetMinerClient.MOD_ID;
import static pom.v1.PomConfig.PomConfig.Config;
import static pom.v1.render.PomPipelines.getHighlight;
import static pom.v1.render.PomPipelines.getLine;


public class PomRendering {

    PomConfig Config = Config();

    private static final PomRendering instance = new PomRendering();

    private static final ByteBufferBuilder allocator = new ByteBufferBuilder(RenderType.SMALL_BUFFER_SIZE);
    private BufferBuilder buffer;


    private static final Vector4f COLOR_MODULATOR = new Vector4f(1, 1, 1, 1);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private MappableRingBuffer vertexBuffer;

    public static PomRendering getInstance() {
        return instance;
    }


    public void extractAndDraw(WorldRenderContext context, Minecraft client, BlockPos pos, VoxelShape shape, boolean timeoutExceeded) {

            RenderPipeline HIGHLIGHT_RENDER = getHighlight();
            RenderPipeline LINES_RENDER = getLine();

            if (Config.highlight.active.get()) {
                renderWaypoint(context, shape, pos, !timeoutExceeded ? Config().highlight.c1.get() : Config().highlight.c2.get(), HIGHLIGHT_RENDER);
                drawFilledThroughWalls(client, HIGHLIGHT_RENDER);
            }
            if (Config.line.active.get()) {
                renderOutline(context, shape, pos, !timeoutExceeded ? Config().line.c1.get() : Config().line.c2.get(), Config.line.width.get(), LINES_RENDER);
                drawFilledThroughWalls(client, LINES_RENDER);
            }
    }

    private void renderOutline(WorldRenderContext context, VoxelShape shape, BlockPos pos, Color color, double lineWidth, RenderPipeline renderPipeline) {
        PoseStack matrices = context.matrices();
        Vec3 camera = context.worldState().cameraRenderState.pos;

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

    private void renderWaypoint(WorldRenderContext context, VoxelShape shape, BlockPos pos, Color color, RenderPipeline renderPipeline) {
        PoseStack matrices = context.matrices();
        Vec3 camera = context.worldState().cameraRenderState.pos;

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
                (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255
        ));
        matrices.popPose();
    }

    private void drawFilledThroughWalls(Minecraft client, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline) {

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

            renderPass.drawIndexed(0 / format.getVertexSize(), 0, drawParameters.indexCount(), 1);
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


    private void renderLine(VertexConsumer buffer, Matrix4f matrix, Vector3f start, Vector3f end, Color color, float width) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        Vector3f lineDir = new Vector3f(end).sub(start).normalize();

        Vector3f toCamera = new Vector3f(0, 0, 1).rotate(camera.rotation());

        Vector3f side = new Vector3f(lineDir).cross(toCamera);

        if (side.lengthSquared() < 0.001f) {
            Vector3f cameraUp = new Vector3f(0, 1, 0).rotate(camera.rotation());
            side = new Vector3f(lineDir).cross(cameraUp);
        }

        side.normalize().mul(width / 2f);

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        buffer.addVertex(matrix, start.x() + side.x(), start.y() + side.y(), start.z() + side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, start.x() - side.x(), start.y() - side.y(), start.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, end.x() - side.x(), end.y() - side.y(), end.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, end.x() + side.x(), end.y() + side.y(), end.z() + side.z()).setColor(r, g, b, a);

        buffer.addVertex(matrix, end.x() + side.x(), end.y() + side.y(), end.z() + side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, end.x() - side.x(), end.y() - side.y(), end.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, start.x() - side.x(), start.y() - side.y(), start.z() - side.z()).setColor(r, g, b, a);
        buffer.addVertex(matrix, start.x() + side.x(), start.y() + side.y(), start.z() + side.z()).setColor(r, g, b, a);
    }
    private void renderFilledBox(Matrix4fc positionMatrix, BufferBuilder buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float red, float green, float blue, float alpha) {
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

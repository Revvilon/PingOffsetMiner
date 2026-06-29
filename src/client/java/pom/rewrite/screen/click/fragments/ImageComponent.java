package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.OwoUIPipelines;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ImageComponent extends TextureComponent {


    public ImageComponent(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }


    protected int maxHeight = 0;

    public TextureComponent maxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    @Override
    public void inflate(Size space) {
        int calculatedWidth = this.horizontalSizing.get().inflate(
                space.width(),
                this::determineHorizontalContentSize
        );

        if (this.maxHeight > 0) {
            calculatedWidth = Math.min(calculatedWidth, this.maxHeight);
        }

        this.width = calculatedWidth;
        this.height = calculatedWidth;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return this.width > 0 ? this.width : this.regionHeight;
    }


    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {
        var visibleArea = this.visibleArea.get();

        int bottomEdge = Math.min(visibleArea.y() + visibleArea.height(), regionHeight);
        int rightEdge = Math.min(visibleArea.x() + visibleArea.width(), regionWidth);

        int srcWidth = rightEdge - visibleArea.x();
        int srcHeight = bottomEdge - visibleArea.y();

        float widthRatio = (float) this.width / this.regionWidth;
        float heightRatio = (float) this.height / this.regionHeight;

        int destWidth = Math.round(srcWidth * widthRatio);
        int destHeight = Math.round(srcHeight * heightRatio);

        graphics.blit(this.blend ? RenderPipelines.GUI_TEXTURED : OwoUIPipelines.GUI_TEXTURED_NO_BLEND,
                this.texture,
                this.x + Math.round(visibleArea.x() * widthRatio),
                this.y + Math.round(visibleArea.y() * heightRatio),
                this.u + visibleArea.x(),
                this.v + visibleArea.y(),
                destWidth,
                destHeight,
                srcWidth,
                srcHeight,
                this.textureWidth, this.textureHeight
        );
    }
}

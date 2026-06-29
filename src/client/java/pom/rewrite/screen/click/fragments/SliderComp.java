package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.component.SlimSliderComponent;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import pom.rewrite.screen.click.ScreenUtil;

import static pom.rewrite.screen.click.ClickGui.accentColor;
import static pom.rewrite.screen.click.ClickGui.textColor;

public class SliderComp extends SlimSliderComponent {


    public SliderComp() {
        super(Axis.HORIZONTAL);
    }

    @Override
    public void draw(OwoUIGraphics graphics, int mouseX, int mouseY, float partialTicks, float delta) {

        int centerY = this.y + (this.height / 2);
        int thickness = 4;
        int trackTop = centerY - (thickness / 2);

        ScreenUtil.fillAndOutline(graphics, accentColor, this.x, trackTop, this.width, thickness, -.3f);

        int thumbWidth = 4;
        int thumbHeight = thickness + 6;
        int thumbTop = centerY - (thumbHeight / 2);

        double ratio = (this.value() - min) / (max - min);
        ratio = Math.max(0.0, Math.min(1, ratio));

        int sliderPos = (int) (this.x + (this.width - 4) * ratio);

        ScreenUtil.fillAndOutline(graphics, textColor, sliderPos, thumbTop, thumbWidth, thumbHeight, .3f);
    }
}

package pom.v1.gui.hud;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import pom.v1.Util;

import static pom.v1.PingOffsetMinerClient.*;
import static pom.v1.PomConfig.PomConfig.Config;

public class TickDisplay extends FlowLayout {

    private final LabelComponent statsLabel;

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "tick_display");

    public TickDisplay() {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);

        this.statsLabel = UIComponents.label(Component.empty());
        this.statsLabel.sizing(Sizing.content());
        this.statsLabel.shadow(true);

        this.positioning.set(Positioning.relative(0, 0));

        this.child(statsLabel);
    }

    @Override
    public void draw(OwoUIGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (!Config().tickDisplay.active.get() || POM_BLOCK.isEmpty() || !Util.shouldRender()) return;

        this.statsLabel.text(Component.literal(POM_CALC.getTicksElapsed() + "/" + POM_CALC.getTicksNeeded()).withColor(POM_CALC.timeoutExceeded() ? Config().tickDisplay.c2.get().getRGB() : Config().tickDisplay.c1.get().getRGB()));

        float scale = Config().tickDisplay.size.get() / 100f;

        this.positioning.set(Positioning.relative(Config().tickDisplay.x.get(), Config().tickDisplay.y.get()));

        int x = this.x();
        int y = this.y();

        context.push();

        context.translate(x, y);

        context.scale(scale, scale);

        context.translate(-x, -y);

        super.draw(context, mouseX, mouseY, partialTicks, delta);

        context.pop();

    }
}

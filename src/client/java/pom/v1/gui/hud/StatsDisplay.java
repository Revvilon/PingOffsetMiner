package pom.v1.gui.hud;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import pom.v1.Util;

import static pom.v1.PingOffsetMinerClient.*;
import static pom.v1.PomConfig.PomConfig.Config;

public class StatsDisplay extends FlowLayout {

    private final LabelComponent statsLabel;

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "stats_display");

    public StatsDisplay() {
        super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);

        this.statsLabel = UIComponents.label(Component.empty());
        this.statsLabel.sizing(Sizing.content());
        this.statsLabel.shadow(true);

        this.positioning.set(Positioning.relative(Config().efficiencyDisplay.x.get(), Config().efficiencyDisplay.y.get()));
        this.padding.set(Insets.of(2));

        this.child(statsLabel);
    }

    @Override
    public void draw(OwoUIGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (!Config().efficiencyDisplay.active.get() || !Util.getIsland()) return;
        updateText();

        float scale = Config().efficiencyDisplay.size.get() / 100f;

        this.positioning.set(Positioning.relative(Config().efficiencyDisplay.x.get(), Config().efficiencyDisplay.y.get()));

        int x = this.x();
        int y = this.y();

        context.push();

        context.translate(x, y);

        context.scale(scale, scale);

        context.translate(-x, -y);

        super.draw(context, mouseX, mouseY, partialTicks, delta);

        context.pop();
    }

    public void updateText() {

        MutableComponent text = Component.empty();

        text.append(Component.empty()
                .append(Component.literal("Uptime: ").withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(String.format("%.1f", POM_EFF.getUpTime())).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal("s\n").withStyle(ChatFormatting.AQUA))));

        text.append(Component.literal("Efficiency: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(String.format("%.1f", POM_EFF.getTempEff() * 100)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("%\n")));

        if (Config().debugGui.get()) {
            text.append(Component.literal("\nMining Speed: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(TOOL_STATS.getSpeed() + "").withStyle(ChatFormatting.WHITE)))

                    .append(Component.literal("\nIs mining: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_EFF.isMining() + "").withStyle(ChatFormatting.WHITE)))

                    .append(Component.literal("\nBlocks mined: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.getTotalMined(POM_BLOCK.getMinedBlocks()) + "").withStyle(ChatFormatting.WHITE)))

                    .append(Component.literal("\nTheoretical max: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.getTotalMined(POM_EFF.getMaxBlocksMined()) +  "").withStyle(ChatFormatting.WHITE)))

                    .append(Component.literal("\nLatest block hit: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.getLastBlockHit()).withStyle(ChatFormatting.WHITE)))

                    .append(Component.literal("\nLatest block mined: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.lastMinedBlock).withStyle(ChatFormatting.WHITE)));
        }

        if (text.equals(this.statsLabel.text())) return;

        this.statsLabel.text(text);
    }
}

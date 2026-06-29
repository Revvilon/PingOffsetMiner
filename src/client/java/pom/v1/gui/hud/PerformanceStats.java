package pom.v1.gui.hud;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import pom.v1.PingOffsetMinerClient;

import static pom.v1.PingOffsetMinerClient.MOD_ID;
import static pom.v1.PomConfig.PomConfig.Config;

public class PerformanceStats extends FlowLayout {

    private final LabelComponent statsLabel;

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "performance_stats");

    public PerformanceStats() {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);

        this.statsLabel = UIComponents.label(Component.empty());
        this.statsLabel.sizing(Sizing.content());
        this.statsLabel.shadow(true);

        this.positioning.set(Positioning.relative(1, 1));
        this.padding(Insets.of(2));

        this.child(statsLabel);
    }

    @Override
    public void draw(OwoUIGraphics context, int mouseX, int mouseY, float partialTicks, float delta) {
        if (!Config().performanceDisplay.active.get()) return;
        updateStats();

        float scale = Config().performanceDisplay.size.get() / 100f;

        context.push();

        context.scale(scale, scale);

        super.draw(context, mouseX, mouseY, partialTicks, delta);

        context.pop();

    }

    private void updateStats() {
        Minecraft client = Minecraft.getInstance();

        int fps = client.getFps();
        int ping = (int) PingOffsetMinerClient.getPing();
        double tps = PingOffsetMinerClient.getTPS();

        var separator = Component.literal(" | ").withStyle(ChatFormatting.GRAY);

        this.statsLabel.text(Component.empty()
                .append(Component.literal("Fps: ").withStyle(ChatFormatting.AQUA)
                    .append(Component.literal(String.valueOf(fps)).withStyle(ChatFormatting.WHITE)))

                .append(separator)

                .append(Component.literal("Ping: ").withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(String.valueOf(ping)).withStyle(ChatFormatting.WHITE)))

                .append(separator)

                .append(Component.literal("Tps: ").withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(String.format("%.1f", tps)).withStyle(ChatFormatting.WHITE)))
        );

    }
}

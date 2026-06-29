package pom.rewrite.screen.hud.elements;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;
import pom.rewrite.features.debug.Profiler;
import pom.rewrite.screen.hud.HudEditScreen;
import pom.rewrite.screen.hud.HudElement;
import pom.rewrite.utility.server.ServerStats;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class ProfilerHud extends FlowLayout implements HudElement {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "profiler");

    private final Feature feature = Profiler.instance;
    private final SettingInt  xPos = Profiler.xPos;
    private final SettingInt yPos =  Profiler.yPos;

    private final LabelComponent label = UIComponents.label(Component.empty());

    public ProfilerHud() {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);
        this.positioning(Positioning.relative(xPos.getInt(), yPos.getInt()));
        this.horizontalAlignment(HorizontalAlignment.LEFT);

        this.tooltip(Component.literal("Profiler"));

        this.child(label);
    }

    private void setTemplate(long ping, double tps) {
        label.text(
                Component.empty()
                        .append(Component.literal("Ping: ").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(String.valueOf(Math.toIntExact(ping))).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal(" | ").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal("TPS: ").withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(String.format("%.1f", tps)).withStyle(ChatFormatting.WHITE))
        );
    }

    private void editLines() {
        setTemplate(120, 20);
    }

    private void updateLines() {
        setTemplate(ServerStats.getPing(), ServerStats.getTps());
    }

    private void clearLines() {
        label.text(Component.empty());
    }

    private boolean isEditing() {
        return Minecraft.getInstance().screen instanceof HudEditScreen;
    }

    @Override
    public void tick() {
        if (isEditing()) {
            editLines();
            return;
        }
        if (!feature.isEnabled()) {
            clearLines();
            return;
        }
        updateLines();
    }

    @Override
    public FlowLayout getLayout() {
        return this;
    }

    @Override
    public void updatePosition(int x, int y) {
        this.xPos.set(x);
        this.yPos.set(y);
        this.positioning(Positioning.relative(x, y));
    }

    @Override
    public int getSavedX() {
        return this.xPos.getInt();
    }

    @Override
    public int getSavedY() {
        return this.yPos.getInt();
    }
}

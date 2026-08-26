package pom.rewrite.screen.hud.elements;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;
import pom.rewrite.features.gui.EfficiencyDisplay;
import pom.rewrite.screen.hud.HudEditScreen;
import pom.rewrite.screen.hud.HudElement;
import pom.rewrite.utility.server.IslandUtils;
import pom.rewrite.utility.stats.EfficiencyStats;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class EfficiencyHud extends FlowLayout implements HudElement {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "efficiency_display");

    private final Feature feature = EfficiencyDisplay.instance;
    private final SettingInt xPos = EfficiencyDisplay.xPos;
    private final SettingInt yPos = EfficiencyDisplay.yPos;
    private final EfficiencyStats effStats = EfficiencyStats.getInstance();
    private final LabelComponent effLabel;
    private final LabelComponent uptimeLabel;

    public EfficiencyHud() {
        super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
        this.positioning(Positioning.relative(xPos.getInt(), yPos.getInt()));

        this.tooltip(Component.literal("Efficiency display"));

        LabelComponent uptimeLabel = UIComponents.label(Component.empty());
        LabelComponent effLabel = UIComponents.label(Component.empty());

        this.uptimeLabel = uptimeLabel;
        this.effLabel = effLabel;

        this.child(uptimeLabel).child(effLabel);

    }

    private boolean isEditing() {
        return Minecraft.getInstance().gui.screen() instanceof HudEditScreen;
    }

    private Component uptimeLine(float uptime) {
        return Component.empty()
                .append(Component.literal("Uptime: ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(String.format("%.1f", uptime)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("s").withStyle(ChatFormatting.AQUA));
    }

    private Component efficiencyLine(int efficiency) {
        return Component.empty()
                .append(Component.literal("Efficiency: ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal(String.valueOf(efficiency)).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("%").withStyle(ChatFormatting.AQUA));
    }

    public void updateLines() {
        this.uptimeLabel.text(uptimeLine(effStats.getUptime()));
        this.effLabel.text(efficiencyLine(effStats.getEfficiency()));
    }

    @Override
    public void tick() {
        if (isEditing()) {
            this.uptimeLabel.text(uptimeLine(30));
            this.effLabel.text(efficiencyLine(100));
            return;
        }
        if (!feature.isEnabled() || !IslandUtils.isIsland() || !EfficiencyStats.getInstance().isMining()) {
            this.uptimeLabel.text(Component.empty());
            this.effLabel.text(Component.empty());
            return;
        }
        this.updateLines();
    }

    @Override
    public io.wispforest.owo.ui.container.FlowLayout getLayout() {
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

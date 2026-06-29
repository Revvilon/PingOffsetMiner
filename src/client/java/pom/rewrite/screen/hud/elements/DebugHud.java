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
import pom.rewrite.features.PingOffsetMiner;
import pom.rewrite.features.debug.Logging;
import pom.rewrite.screen.hud.HudEditScreen;
import pom.rewrite.screen.hud.HudElement;
import pom.rewrite.utility.block.BlockData;
import pom.rewrite.utility.server.IslandUtils;
import pom.rewrite.utility.stats.MiningStats;
import pom.rewrite.utility.stats.TickStats;

import java.util.ArrayList;
import java.util.List;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class DebugHud extends FlowLayout implements HudElement {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "debug_display");

    private final Feature feature = Logging.instance;
    private final SettingInt xPos = Logging.xPos;
    private final SettingInt yPos = Logging.yPos;

    private final List<LabelComponent> labels = new ArrayList<>();

    public DebugHud() {
        super(Sizing.content(), Sizing.content(), Algorithm.VERTICAL);
        this.positioning(Positioning.relative(xPos.getInt(),  yPos.getInt()));
        this.horizontalAlignment(HorizontalAlignment.LEFT);

        this.tooltip(Component.literal("Debug hud"));

        for (int i = 0; i < 7; i++) {
            LabelComponent line = UIComponents.label(Component.empty());
            labels.add(line);
            this.child(line);
        }
    }

    private void templateLines(boolean isEnabled, boolean island, int speed, int ticks, String block, int cd, boolean shouldRender) {
        String[] lines = {
                "Enabled: " + isEnabled,
                "Island: " + island,
                "Mining Speed: " + speed,
                "Ticks: " + ticks,
                "Block: " + block,
                "CD: " + cd,
                "ShouldRender: " + shouldRender
        };

        for (int i = 0; i < lines.length; i++) {
            labels.get(i).text(Component.literal(lines[i]).withStyle(ChatFormatting.RED));
        }
    }

    private void updateLines() {
        templateLines(
                PingOffsetMiner.instance.isEnabled(),
                IslandUtils.isIsland(),
                MiningStats.instance().getMiningSpeed(),
                TickStats.instance().ticksNeeded(),
                BlockData.getInstance().getCurrentBlock() == null ? "Null" : BlockData.getInstance().getCurrentBlock().id,
                MiningStats.instance().getMsbCooldown(),
                MiningStats.instance().shouldRender()
        );
    }

    private void updateEditLines() {
        templateLines(true, true, 10000, 20, "Block", 20, true);
    }

    private boolean isEditing() {
        return Minecraft.getInstance().screen instanceof HudEditScreen;
    }

    private void clearLines() {
        for (LabelComponent label : labels) {
            label.text(Component.empty());
        }
    }

    @Override
    public void tick() {
        if (isEditing()) {
            updateEditLines();
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

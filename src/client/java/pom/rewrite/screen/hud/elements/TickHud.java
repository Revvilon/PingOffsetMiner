package pom.rewrite.screen.hud.elements;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;
import pom.rewrite.features.gui.TickDisplay;
import pom.rewrite.screen.hud.HudEditScreen;
import pom.rewrite.screen.hud.HudElement;
import pom.rewrite.utility.stats.MiningStats;
import pom.rewrite.utility.stats.TickStats;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;


public class TickHud extends FlowLayout implements HudElement {

    private final LabelComponent label;
    public static final Identifier ID = Identifier.fromNamespaceAndPath(MOD_ID, "tick_display");


    private final Feature feature = TickDisplay.instance;
    private final SettingInt xPos = TickDisplay.xPos;
    private final SettingInt yPos =  TickDisplay.yPos;
    private final int preMined = TickDisplay.preMined.getColor().argb();
    private final int postMined = TickDisplay.postMined.getColor().argb();

    public TickHud() {
        super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);
        this.positioning(Positioning.relative(xPos.getInt(), yPos.getInt()));

        this.label = UIComponents.label(Component.empty()).shadow(true);
        this.tooltip(Component.literal("Tick display"));

        this.child(label);
    }

    private final TickStats ticks =  TickStats.instance();
    private final MiningStats miningStats = MiningStats.instance();

    private Component tickString() {
        int color = ticks.timeoutExceeded() ? postMined : preMined;
        String tickString = ticks.ticksElapsed() + "/" + ticks.ticksNeeded();
        return Component.literal(tickString).withColor(color);
    }

    private boolean isEditing() {
        return Minecraft.getInstance().screen instanceof HudEditScreen;
    }

    @Override
    public void tick() {
        if (isEditing()) {
            return;
        }

        if (!feature.isEnabled() || !miningStats.shouldRender()) {
            this.label.text(Component.empty());
            return;
        }
        this.label.text(tickString());
    }

    @Override
    public FlowLayout getLayout() {
        if (isEditing()) {
            this.label.text(Component.literal("x/y"));
        }
        return this;
    }

    @Override
    public void updatePosition(int x, int y) {
        xPos.set(x);
        yPos.set(y);
        this.positioning(Positioning.relative(xPos.getInt(), yPos.getInt()));
    }

    @Override
    public int getSavedX() {
        return xPos.getInt();
    }

    @Override
    public int getSavedY() {
        return yPos.getInt();
    }
}

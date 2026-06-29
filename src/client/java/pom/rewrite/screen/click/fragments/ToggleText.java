package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import pom.rewrite.screen.click.ScreenUtil;

import static pom.rewrite.screen.click.ClickGui.accentColor;
import static pom.rewrite.screen.click.ClickGui.mainColor;

public class ToggleText extends ButtonComponent {

    private final EventStream<ToggleButton.ToggleChanged> changedEvents = ToggleButton.ToggleChanged.newStream();
    String enabled, disabled;
    boolean toggle;

    public ToggleText(String enabled, String disabled, boolean toggle) {
        super(Component.literal(" "), press -> {});

        this.toggle = toggle;
        this.enabled = enabled;
        this.disabled = disabled;

        this.onPress(button -> this.setToggle());

        this.renderer(((context, button, delta) -> {
            Color tempColor = this.toggle ? accentColor : mainColor;

            if (button.isHoveredOrFocused()) {
                tempColor = ScreenUtil.brighten(tempColor, 1.3f);
            }

            ScreenUtil.fillAndOutline(context, tempColor, button.x(), button.y(), button.width(), button.height());
        }));

    }

    @Override
    public void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractContents(context, mouseX, mouseY, delta);

        this.setMessage(Component.literal(this.toggle ? enabled : disabled));

        this.sizing(Sizing.fixed((int) (this.height*1.5)), Sizing.content());
    }

    public void setToggle() {
        this.setToggle(!this.toggle);
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        changedEvents.sink().onToggle(this.toggle);
    }

    public EventSource<ToggleButton.ToggleChanged> onToggled() {
        return changedEvents.source();
    }

    public interface ToggleChanged {
        static EventStream<ToggleButton.ToggleChanged> newStream() {
            return new EventStream<>(subscribers -> toggle -> {
                for (var subscriber : subscribers) {
                    subscriber.onToggle(toggle);
                }
            });
        }

        void onToggle(boolean toggle);
    }
}

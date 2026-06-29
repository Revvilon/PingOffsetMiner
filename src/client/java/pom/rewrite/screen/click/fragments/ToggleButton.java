package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import pom.rewrite.screen.click.ScreenUtil;

import static pom.rewrite.screen.click.ClickGui.*;

public class ToggleButton extends ButtonComponent {
    private final EventStream<ToggleChanged> changedEvents = ToggleChanged.newStream();
    private boolean toggle;

    private final Color disabledColor = secondaryColor;
    private final Color enabledColor =  accentColor;


    public ToggleButton(String name, boolean toggled) {
        super(Component.literal(" ").withStyle(style -> style.withColor(0x00000000)), _ -> {});
        this.toggle = toggled;
        this.onPress(button -> this.setToggle());

        this.renderer((context, button, _) -> {

            Color tempColor = this.toggle ? enabledColor : disabledColor;

            if (button.isHoveredOrFocused()) {
                tempColor = ScreenUtil.brighten(tempColor, 1.3f);
            }

            ScreenUtil.drawSwitch(context, tempColor, textColor, button.x(), button.y(), button.width(), button.height(), this.toggle);

        });
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);


        this.sizing(Sizing.fixed((int) (this.height*1.5)), Sizing.content());
    }

    public void setToggle() {
        this.setToggle(!this.toggle);
    }

    public void setToggle(boolean toggle) {
        this.toggle = toggle;
        changedEvents.sink().onToggle(this.toggle);
    }

    public EventSource<ToggleChanged> onToggled() {
        return changedEvents.source();
    }

    public interface ToggleChanged {
        static EventStream<ToggleChanged> newStream() {
            return new EventStream<>(subscribers -> toggle -> {
                for (var subscriber : subscribers) {
                    subscriber.onToggle(toggle);
                }
            });
        }

        void onToggle(boolean toggle);
    }
}

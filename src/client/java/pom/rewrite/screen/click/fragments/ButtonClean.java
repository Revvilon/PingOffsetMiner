package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.network.chat.Component;
import pom.rewrite.screen.click.ScreenUtil;

import java.util.function.Consumer;

import static pom.rewrite.screen.click.ClickGui.accentColor;

public class ButtonClean extends ButtonComponent {
    public ButtonClean(String text, Consumer<ButtonComponent> callback) {
        super(Component.literal(text), callback);

        this.renderer((context, button, delta) -> {

            Color tempColor = accentColor;

            if (button.isHoveredOrFocused()) {
                tempColor = ScreenUtil.brighten(tempColor, 1.3f);
            }

            ScreenUtil.fillAndOutline(context, tempColor, button.x(), button.y(), button.width(), button.height());

        });
    }
}

package pom.rewrite.screen.click.fragments;

import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.network.chat.Component;
import pom.rewrite.screen.click.ScreenUtil;


import static pom.rewrite.screen.click.ClickGui.accentColor;

public class EnumClicker<T extends Enum<T>> extends ButtonComponent {
    private final T[] values;
    private final T defaultValue;
    private T value;
    private final EventStream<TextBoxComponent.OnChanged> changedEvents = TextBoxComponent.OnChanged.newStream();

    public EnumClicker(String value, T defaultValue, Class<T> values) {
        super(Component.empty(), _ -> {});

        this.values = values.getEnumConstants();
        this.defaultValue = defaultValue;

        this.setValue(value);

        this.renderer((context, button, delta) -> {

            Color tempColor = accentColor;

            if (button.isHoveredOrFocused()) {
                tempColor = ScreenUtil.brighten(tempColor, 1.3f);
            }

            ScreenUtil.fillAndOutline(context, tempColor, button.x(), button.y(), button.width(), button.height());}
        );

        this.onPress(button -> {
           for (int i = 0; i < this.values.length; i++) {
               if (this.values[i].equals(this.value)) {
                   T newValue = i == this.values.length -1 ? this.values[0] : this.values[i + 1];
                    this.setValue(newValue);
                    changedEvents.sink().onChanged(newValue.name());
                    return;
               }
           }
            this.setValue(this.defaultValue);
            changedEvents.sink().onChanged(this.defaultValue.name());
        });
    }

    public void setValue(String value) {
        this.setValue(this.asValue(value));
    }

    public void setValue(T value) {
        this.value = value;
        this.setMessage(Component.nullToEmpty(value.name()));
        this.changedEvents.sink().onChanged(this.value.name());
    }

    public T asValue(String name) {
        for (T value : this.values) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return this.defaultValue;
    }

    public EventSource<TextBoxComponent.OnChanged> onChanged() {
        return changedEvents.source();
    }
}

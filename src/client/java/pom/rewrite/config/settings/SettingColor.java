package pom.rewrite.config.settings;

import io.wispforest.owo.ui.core.Color;
import pom.rewrite.config.Feature;


public class SettingColor extends SettingGeneric {
    public SettingColor(Object defValue, String name, String parentName) {
        super(defValue, name, parentName);
    }

    public SettingColor(Object defValue, String name, Feature parentInstance) {
        this(defValue, name, parentInstance.name());
    }

    public Color getColor() {
        return Color.ofArgb(this.get().getAsInt());
    }
}

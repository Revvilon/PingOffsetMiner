package pom.rewrite.features.gui;

import io.wispforest.owo.ui.core.Color;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingColor;
import pom.rewrite.config.settings.SettingInt;


public class TickDisplay {
    public static final Feature instance = new Feature("tickDisplay", true);

    public static final SettingInt xPos = new SettingInt(50, "xPos", instance);
    public static final SettingInt yPos = new SettingInt(55, "yPos", instance);

    public static final SettingInt scale = new  SettingInt(1, "scale", instance);

    public static final SettingColor preMined = new SettingColor(new Color(1f, 0f, 0f), "preMinedColor", instance);
    public static final SettingColor postMined = new SettingColor(new Color(0f, 1f, 0f), "postMinedColor", instance);

}

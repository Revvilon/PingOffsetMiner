package pom.rewrite.features.gui;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;

public class EfficiencyDisplay {
    public static final Feature instance = new Feature("efficiencyDisplay", true);

    public static final SettingInt xPos = new SettingInt(1, "xPos", instance);
    public static final SettingInt yPos = new SettingInt(50, "yPos", instance);

    public static final SettingInt timeout = new SettingInt(30, "timeout", instance);

    public static final SettingInt scale = new SettingInt(1, "scale", instance);

}

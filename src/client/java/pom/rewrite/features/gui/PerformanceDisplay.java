package pom.rewrite.features.gui;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;
import pom.rewrite.config.settings.SettingNum;

public class PerformanceDisplay {
    public static final Feature instance = new Feature("performanceDisplay", false);

    public static final SettingNum xPos = new SettingNum(1, "xPos", instance);
    public static final SettingNum yPos = new SettingNum(1, "yPos", instance);

    public static final SettingInt scale = new SettingInt(1, "scale", instance);
}

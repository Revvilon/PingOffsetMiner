package pom.rewrite.features.debug;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;
import pom.rewrite.config.settings.SettingNum;

public class CustomStats {
    public static final Feature instance = new Feature("customStats", false);
    public static final SettingInt customSpeed = new SettingInt(6000, "customSpeed", instance);
    public static final SettingInt customPing = new SettingInt(100, "customPing", instance);
    public static final SettingNum customTps = new SettingNum(20.0, "customTps", instance);

    public static final SettingInt extraSpeed = new SettingInt(855, "extraSpeed", instance);
    public static final SettingInt extraSpeedMetal = new SettingInt(0, "extraSpeedMetal", instance);
    public static final SettingInt extraSpeedOre = new SettingInt(0, "extraSpeedOre", instance);

    public static final SettingInt tickMargin = new SettingInt(0, "tickMargin", instance);
}

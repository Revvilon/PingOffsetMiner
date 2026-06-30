package pom.rewrite.features.debug;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;

public class Logging {
    public static final Feature instance = new Feature("logging", false);
    public static final Feature igetit = new Feature("igetit", false);

    public static final SettingInt xPos = new SettingInt(30, "xPos", instance);
    public static final SettingInt yPos = new SettingInt(50, "yPos", instance);
}

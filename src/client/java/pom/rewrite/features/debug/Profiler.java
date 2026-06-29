package pom.rewrite.features.debug;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingInt;

public class Profiler {
    public static final Feature instance = new Feature("profiler", false);

    public static final SettingInt xPos = new SettingInt(1, "xPos", instance);
    public static final SettingInt yPos = new SettingInt(1, "yPos", instance);
}

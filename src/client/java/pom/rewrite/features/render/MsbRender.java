package pom.rewrite.features.render;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingBoolean;

public class MsbRender {
    public static final Feature instance = new Feature("msbRender", false);
    public static final SettingBoolean msbToggle = new SettingBoolean(false, "msbToggle", instance);
}

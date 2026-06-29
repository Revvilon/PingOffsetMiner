package pom.rewrite.features.render;

import io.wispforest.owo.ui.core.Color;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingBoolean;
import pom.rewrite.config.settings.SettingColor;
import pom.rewrite.config.settings.SettingNum;


public class OutlineRender {
    public static final Feature instance = new Feature("outlineRender", true);
    public static final SettingBoolean depthToggle = new SettingBoolean(true, "depthTest", instance);
    public static final SettingColor preMined = new SettingColor(new Color(1f, 0f, 0f, 1f), "colorPreMined", instance);
    public static final SettingColor postMined = new SettingColor(new Color(0f, 1f, 0f, 1f), "colorPostMined", instance);
    public static final SettingNum lineWidth = new SettingNum(1.0f, "lineWidth", instance);
}

package pom.rewrite.features.render;

import io.wispforest.owo.ui.core.Color;
import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingBoolean;
import pom.rewrite.config.settings.SettingColor;

public class HighlightRender {
    public static final Feature instance = new Feature("highlightRender", true);
    public static final SettingBoolean depthToggle = new SettingBoolean(true, "depthTest", instance);
    public static final SettingColor preMined = new SettingColor(new Color(1f, 0f, 0f, .2f), "colorPreMined", instance);
    public static final SettingColor postMined = new SettingColor(new Color(0f, 1f, 0f, .2f), "colorPostMined", instance);
}

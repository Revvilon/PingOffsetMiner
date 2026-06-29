package pom.rewrite.features.sound;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingString;

public class SoundAlert {
    public static final Feature instance = new Feature("soundAlert", false);
    public static final SettingString soundPath = new SettingString("", "soundPath", instance);


}

package pom.rewrite.features.toggles;

import pom.rewrite.config.Feature;
import pom.rewrite.config.settings.SettingHash;
import pom.rewrite.utility.server.IslandUtils;

import java.util.Map;

public class IslandToggles {
    public static final Feature instance = new Feature("islandToggles");
    public static final SettingHash islands = new SettingHash(getDefaults(), "islands", instance);

    private static Map<String, Boolean> getDefaults() {
        return IslandUtils.getIslands();
    }
}

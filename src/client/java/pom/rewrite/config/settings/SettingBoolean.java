package pom.rewrite.config.settings;

import pom.rewrite.config.Feature;

public class SettingBoolean extends SettingGeneric {
    public SettingBoolean(Object defValue, String name, String parentName) {
        super(defValue, name, parentName);
    }

    public SettingBoolean(Object defValue, String name, Feature parentInstance) {
        this(defValue, name, parentInstance.name());
    }

    public boolean getBool() {
        return this.get().getAsBoolean();
    }
}

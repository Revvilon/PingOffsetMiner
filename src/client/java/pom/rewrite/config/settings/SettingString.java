package pom.rewrite.config.settings;

import pom.rewrite.config.Feature;

public class SettingString extends SettingGeneric {

    public SettingString(Object defValue, String name, String parentName) {
        super(defValue, name, parentName);
    }

    public SettingString(Object defValue, String name, Feature parentInstance) {
        this(defValue, name, parentInstance.name());
    }

    public String getString() {
        return this.get().getAsString();
    }
}

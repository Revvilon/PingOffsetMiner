package pom.rewrite.config.settings;

import pom.rewrite.config.Feature;

public class SettingInt extends SettingGeneric {
    public SettingInt(Object defValue, String name, String parentName) {
        super(defValue, name, parentName);
    }

    public SettingInt(Object defValue, String name, Feature parentInstance) {
        this(defValue, name, parentInstance.name());
    }

    public int getInt() {
        return this.get().getAsInt();
    }
}

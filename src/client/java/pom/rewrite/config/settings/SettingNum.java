package pom.rewrite.config.settings;

import pom.rewrite.config.Feature;

public class SettingNum extends SettingGeneric {
    public SettingNum(Object defValue, String name, String parentName) {
        super(defValue, name, parentName);
    }

    public SettingNum(Object defValue, String name, Feature parentInstance) {
        this(defValue, name, parentInstance.name());
    }

    public double getDouble() {
        return this.get().getAsDouble();
    }

    public float getFloat() {
        return this.get().getAsFloat();
    }
}

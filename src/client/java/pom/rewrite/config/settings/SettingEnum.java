package pom.rewrite.config.settings;

import net.minecraft.core.ClientAsset;
import pom.rewrite.config.Feature;

public class SettingEnum<T extends Enum<T>> extends SettingGeneric{

    public final Class<T> values;
    public final T[] constants;
    private T current;

    public SettingEnum(T defValue, Class<T> values, String name, String parentName) {
        super(defValue, name, parentName);
        this.values = values;
        this.constants = values.getEnumConstants();
        this.current = this.toConstants(this.get().getAsString());
    }

    public SettingEnum(T defValue, Class<T> values, String name, Feature parent) {
        this(defValue, values, name, parent.name());
    }

    public T toConstants(String value) {
        for (T constant : this.constants) {
            if (constant.name().equals(value)) {
                return constant;
            }
        }
        return this.constants[0];
    }

    public T value() {
        String value = this.get().getAsString();
        if (!this.current.name().equals(value)) {
            this.current = this.toConstants(value);
        }
        return this.current;
    }

    public T defaultValue() {
        return this.toConstants(this.get().getAsString());
    }
}

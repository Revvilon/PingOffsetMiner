package pom.rewrite.config.settings;

import pom.rewrite.config.Feature;

import java.util.Set;

public class SettingList<T> extends SettingGeneric {
    public final Set<T> values;

    public SettingList(Set<T> values, String name, String parentName) {
        super(values, name, parentName);

        this.values = values;
    }

    public SettingList(Set<T> values, String name, Feature parent) {
        this(values, name, parent.name());
    }

    public Set<T> getSet() {
        return Set.copyOf(this.values);
    }

    public void addValue(T value) {
        this.values.add(value);
    }

    public void removeValue(T value) {
        this.values.remove(value);
    }

    public void clearValues() {
        this.values.clear();
    }
}

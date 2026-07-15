package pom.rewrite.config.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import pom.rewrite.PingOffsetMinerClient;
import pom.rewrite.config.Feature;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class SettingList<T> extends SettingGeneric {
    private final Set<T> values;
    private final Function<JsonElement, T> deserializer;

    public SettingList(Set<T> values, String name, String parentName, Function<JsonElement, T> deserializer) {
        super(values, name, parentName);
        this.values = new LinkedHashSet<>(values);
        this.deserializer = deserializer;
        this.syncFromJSON();
    }

    public SettingList(Set<T> values, String name, Feature parent, Function<JsonElement, T> deserializer) {
        this(values, name, parent.name, deserializer);
    }

    private void syncFromJSON() {
        JsonElement currentJson = this.get();
        if (currentJson != null && currentJson.isJsonArray()) {
            this.values.clear();
            JsonArray array = currentJson.getAsJsonArray();
            for (JsonElement element : array) {
                try {
                    this.values.add(this.deserializer.apply(element));
                } catch (Exception e) {
                    PingOffsetMinerClient.LOGGER.error("Failed to parse config element: {} for setting {}", element, this.parent);
                }
            }
        }
    }

    public Set<T> getSet() {
        this.syncFromJSON();
        return Set.copyOf(this.values);
    }

    public void addValue(T value) {
        this.syncFromJSON();
        if (this.values.add(value)) {
            this.save();
        }
    }

    public void removeValue(T value) {
        this.syncFromJSON();
        if (this.values.remove(value)) {
            this.save();
        }
    }

    public void clearValues() {
        this.values.clear();
        this.save();
    }

    private void save() {
        this.set(this.values);
    }
}

package pom.rewrite.config.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pom.rewrite.config.Feature;

import java.util.LinkedHashMap;
import java.util.Map;

public class SettingHash extends SettingGeneric {

    private final Map<String, Boolean> runtimeHash = new LinkedHashMap<>();
    private final Map<String, Boolean> defaults;

    public SettingHash(Map<String, Boolean> defValue, String name, String parentName) {
        super(parseMap(defValue), name, parentName);

        this.defaults = new LinkedHashMap<>(defValue);
        this.loadMapFromValue();
    }

    public SettingHash(Map<String, Boolean> defValue, String name, Feature parent) {
        this(defValue, name, parent.name());
    }

    private static JsonObject parseMap(Map<String, Boolean> map) {
     JsonObject jsonObject = new JsonObject();
     if (map != null) {
         map.forEach(jsonObject::addProperty);
     }
     return jsonObject;
    }

    private void loadMapFromValue() {
        this.runtimeHash.clear();
        JsonElement currentElement = super.get();

        if (currentElement instanceof JsonObject jsonObject) {
            for (String key : this.defaults.keySet()) {
                JsonElement val = jsonObject.get(key);

                if (val == null || val.isJsonNull() ||!val.getAsJsonPrimitive().isBoolean()) {
                    this.runtimeHash.put(key, this.defaults.get(key));
                    continue;
                }

                this.runtimeHash.put(key, val.getAsBoolean());
            }

            jsonObject.entrySet().forEach(entry -> {
                if (!this.runtimeHash.containsKey(entry.getKey())) {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isBoolean()) {
                        this.runtimeHash.put(entry.getKey(), entry.getValue().getAsBoolean());
                    }
                }
            });
        } else {
            this.runtimeHash.putAll(this.defaults);
        }
    }

    public void set(String key,  boolean newVal) {
        this.update();
        this.loadMapFromValue();

        this.runtimeHash.put(key, newVal);

        this.set(parseMap(runtimeHash));
    }

    public boolean isEnabled(String key) {
        this.update();
        this.loadMapFromValue();

        return this.runtimeHash.getOrDefault(key, false);
    }

    public Map<String, Boolean> getMap() {
        this.loadMapFromValue();
        return this.runtimeHash;
    }
}

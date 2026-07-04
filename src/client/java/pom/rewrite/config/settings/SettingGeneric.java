package pom.rewrite.config.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.wispforest.owo.ui.core.Color;
import pom.rewrite.config.ConfigHandler;

public class SettingGeneric {
    public String name;
    public String parent;
    private JsonElement value;
    private JsonElement defaultValue;
    private int hash = 0;

    public SettingGeneric(Object defValue, String name, String parentName) {
        this.name = name;
        this.parent = parentName;
        this.defaultValue = this.parse(defValue);
        this.value = defaultValue;
    }

    public final JsonElement parse(Object value) {
        return switch (value) {
            case Boolean bool -> new JsonPrimitive(bool);
            case String string -> new JsonPrimitive(string);
            case Number number -> new JsonPrimitive(number);
            case JsonObject jsonObject -> jsonObject;
            case Enum<?> en ->  new JsonPrimitive(en.name());
            case Color color -> new JsonPrimitive(color.argb());

            case Iterable<?> iterable -> {
                JsonArray array = new JsonArray();
                for (Object o : iterable) {
                    array.add(this.parse(o));
                }
                yield array;
            }

            default ->
                throw new IllegalArgumentException("Cannot parse " + value);
        };
    }

    public final void update() {
        if (this.hash == ConfigHandler.getHash()) return;
        if (ConfigHandler.get().has(this.parent)) {
            JsonObject data = ConfigHandler.get().getAsJsonObject(parent);
            this.value = data.has(this.name) ? data.get(this.name) : this.defaultValue;
        }
        this.hash = ConfigHandler.getHash();
    }

    public final JsonElement get() {
        this.update();
        return this.value;
    }

    public void set(JsonElement value) {
        if (!ConfigHandler.get().has(this.parent)) {
            ConfigHandler.get().add(parent, new JsonObject());
        }
        this.value = value;
        ConfigHandler.get().getAsJsonObject(this.parent).add(this.name, this.value);
        ConfigHandler.computeHash();
    }

    public void set(Object value) {
        this.set(this.parse(value));
    }

    public void reset() {
        this.set(this.defaultValue);
    }
}

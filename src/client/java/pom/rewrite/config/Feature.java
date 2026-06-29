package pom.rewrite.config;

import com.google.gson.JsonObject;

public class Feature {
    public String name;
    private boolean enabled;
    private final boolean defaultEnabled;
    private int hash = 0;

    public Feature(String name, boolean enabled) {
        this.name = name;
        this.defaultEnabled = enabled;
        this.enabled = enabled;
    }

    public Feature(String name) {
        this(name, false);
    }

    public String name() {
        return name;
    }

    public void update() {
            if (this.hash == ConfigHandler.getHash()) return;
            if (ConfigHandler.get().has(this.name)) {
                JsonObject data = ConfigHandler.get().get(this.name).getAsJsonObject();
                this.enabled = data.has("enabled") ? data.get("enabled").getAsBoolean() : this.defaultEnabled;
            }
            this.hash = ConfigHandler.getHash();
    }

    public boolean isEnabled() {
        this.update();
        return this.enabled;
    }

    public void setEnabled(boolean newVal) {
        if (!ConfigHandler.get().has(this.name)) {
            ConfigHandler.get().add(this.name, new JsonObject());
        }
        this.enabled = newVal;
        ConfigHandler.get().get(this.name).getAsJsonObject().addProperty("enabled", newVal);
        ConfigHandler.computeHash();
    }

    public void reset() {
        this.setEnabled(defaultEnabled);
    }
}

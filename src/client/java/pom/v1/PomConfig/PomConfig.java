package pom.v1.PomConfig;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.HashMap;

public class PomConfig {
    public static ConfigClassHandler<PomConfig> HANDLER = ConfigClassHandler.createBuilder(PomConfig.class)
            .id(Identifier.withDefaultNamespace("ping-offset-miner"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("pomConfig.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .build())
            .build();


    public static PomConfig Config() {
        return HANDLER.instance();
    }

    @SerialEntry
    public renderSettings line = renderSettings.lines();
    @SerialEntry
    public renderSettings highlight = renderSettings.highlight();


    @SerialEntry
    public boolean active = true;

    @SerialEntry
    public boolean sound = false;

    @SerialEntry
    public String soundpath = "";

    @SerialEntry
    public boolean debug = false;

    @SerialEntry
    public double speed = 0.0;

    @SerialEntry
    public double ping = 0.0;

    @SerialEntry
    public boolean extra = true;

    @SerialEntry
    public double extraVal = 855;

    @SerialEntry
    public HashMap<String, Boolean> blockEnabled = new HashMap<>() {
    };

    @SerialEntry
    public HashMap<String, Boolean> islandEnabled = new HashMap<>();

    @SerialEntry
    public boolean ability = true;

    @SerialEntry
    public boolean logging = false;

    @SerialEntry
    public msbToggle msbToggleValue = msbToggle.OFF;
    @SerialEntry
    public enum msbToggle {
        OFF("Turn off"),
        ON("Turn on");

        private final String name;

        msbToggle(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @SerialEntry
    public boolean shouldWarn = true;


    public static void init() {
        HANDLER.load();
    }
    public void save() {
        HANDLER.save();
    }
}


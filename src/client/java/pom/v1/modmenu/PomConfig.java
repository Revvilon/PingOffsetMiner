package pom.v1.modmenu;

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
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("pom.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .build())
            .build();

    @SerialEntry
    public Color color1 = new Color(255, 0,0, 255);

    @SerialEntry
    public Color color2 = new Color(0, 255, 0, 255);

    @SerialEntry
    public Color blockCol1 = new Color(255, 0, 0, 50);

    @SerialEntry
    public Color blockCol2 = new Color(0, 255, 0, 50);

    @SerialEntry
    public boolean lineactive = true;

    @SerialEntry
    public double lineWidth = 1.0;

    @SerialEntry
    public boolean blockactive = true;

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
    public double extraVal = 855;

    @SerialEntry
    public HashMap<String, Boolean> blockEnabled = new HashMap<String, Boolean>() {};

    @SerialEntry
    public boolean ability = true;

    @SerialEntry
    public boolean logging = false;

    @SerialEntry

    public msbToggle msbToggleValue = msbToggle.OFF;
    public enum msbToggle {
        OFF("Turn off"),
        ON("Turn on");

        private final String name;
        msbToggle(String name) {
            this.name = name;
        }
        @Override public String toString() {return name;}
    }


    public static void init() {
        HANDLER.load();
    }
    public static void save() {
        HANDLER.save();
    }
}

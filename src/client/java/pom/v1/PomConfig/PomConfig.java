package pom.v1.PomConfig;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import pom.v1.PomConfig.dataHolder.renderSettings;
import pom.v1.PomConfig.dataHolder.textSettings;
import pom.v1.pomGetter.PomBlocks;
import pom.v1.pomGetter.PomIslandData;

import java.util.HashMap;
import java.util.Map;

public class PomConfig {
    public static ConfigClassHandler<PomConfig> HANDLER = ConfigClassHandler.createBuilder(PomConfig.class)
            .id(Identifier.withDefaultNamespace("ping-offset-miner"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("pomConfig.json5"))
                    .appendGsonBuilder(builder -> {
                        builder.setPrettyPrinting();
                        builder.serializeNulls();
                        return builder;
                    })
                    .build())
            .build();

    private static PomConfig INSTANCE;


    public static PomConfig Config() {
        if (INSTANCE == null) {
            HANDLER.load();
            INSTANCE = HANDLER.instance();
        }
        return INSTANCE;
    }

    public PomConfig() {
    }

    @SerialEntry
    public Property<Boolean> active = new Property<>(true);

    @SerialEntry
    public renderSettings line = renderSettings.lines();

    @SerialEntry
    public renderSettings highlight = renderSettings.highlight();

    @SerialEntry
    public Property<Boolean> shouldLog = new Property<>(false);

    @SerialEntry
    public Property<Boolean> sound = new Property<>(false);

    @SerialEntry
    public Property<String> soundpath = new Property<>("");

    @SerialEntry
    public Property<Boolean> debug = new Property<>(false);

    @SerialEntry
    public Property<Double> speed = new Property<>(0.0);

    @SerialEntry
    public Property<Double> ping = new Property<>(0.0);

    @SerialEntry
    public Property<Double> tps = new Property<>(0.0);

    @SerialEntry
    public Property<Boolean> extra = new Property<>(true);

    @SerialEntry
    public Property<Double> extraVal = new Property<>(855.0);

    @SerialEntry
    public HashMap<String, Boolean> blockEnabled = PomBlocks.getBlocks();

    @SerialEntry
    public Map<String, Boolean> islandEnabled = PomIslandData.getIslands();

    @SerialEntry
    public textSettings tickDisplay = textSettings.tickDisplay();

    @SerialEntry
    public textSettings efficiencyDisplay = textSettings.efficiencyDisplay();

    @SerialEntry
    public Property<Integer> efficiencyDisplaySec = new Property<>(30);

    @SerialEntry
    public Property<Boolean> ability = new Property<>(false);

    @SerialEntry
    public Property<Boolean> debugGui = new Property<>(false);

    @SerialEntry
    public Property<msbToggle> msbToggleValue = new Property<>(msbToggle.OFF);
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
    public Property<Boolean> shouldWarn = new Property<>(true);

    public void validate() {
        PomBlocks.getBlocks().forEach((key, val) -> blockEnabled.putIfAbsent(key, val));
        PomIslandData.getIslands().forEach((key, val) -> islandEnabled.putIfAbsent(key, val));

    }

    public static void init() {
        HANDLER.load();
        Config().validate();
        INSTANCE = HANDLER.instance();
        HANDLER.save();
    }
    public void save() {
        HANDLER.save();
    }

    public static class Property<T> {
        @SerialEntry
        public T value;

        @SerialEntry
        public T defaultValue;

        public Property(T val) {
            this.value = val;
            this.defaultValue = val;
        }

        public T get() { return value; }

        public T getDefault() {
            return defaultValue == null ? value : defaultValue;
        }

        public void set(T value) { this.value = value; }
    }
}


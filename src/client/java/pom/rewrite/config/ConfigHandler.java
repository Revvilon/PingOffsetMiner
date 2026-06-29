package pom.rewrite.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import pom.rewrite.PingOffsetMinerClient;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path DIR = FabricLoader.getInstance().getConfigDir();
    private static final String NAME = "pom_config.json";
    private static final Path FILE = DIR.resolve(NAME);
    private static JsonObject DATA = new JsonObject();
    private static int HASH = 0;

    public static void load() {
        if (Files.exists(FILE)) {
            try {
                DATA = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            } catch (Exception e) {
                PingOffsetMinerClient.LOGGER.error("Failed to load POM config file!", e);
            }
        }  else {
            save();
        }
        computeHash();
    }

    public static void save() {
        Path TEMP = DIR.resolve(NAME + "_temp_" + System.currentTimeMillis() + ".json");
        try {

            if (!Files.exists(TEMP.getParent())) {
                Files.createDirectories(TEMP.getParent());
            }

            Files.writeString(TEMP, GSON.toJson(DATA));
            try {
                Files.move(TEMP, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(TEMP, FILE, StandardCopyOption.REPLACE_EXISTING);
            }

            Files.deleteIfExists(TEMP);

        } catch (IOException e) {

            PingOffsetMinerClient.LOGGER.error("Failed to save POM config file!", e);
        }
    }

    public static void saveAsync() {
        Thread.startVirtualThread(ConfigHandler::save);
    }

    public static int getHash() {
        return HASH;
    }

    public static void computeHash() {
        HASH = DATA.hashCode();
    }

    public static JsonObject get() {
        return DATA;
    }
}

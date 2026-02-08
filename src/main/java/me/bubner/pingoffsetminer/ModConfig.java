package me.bubner.pingoffsetminer;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Mod configuration save/loader.
 */
public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("pom.properties");
    public final Properties properties = new Properties();

    public void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (var reader = Files.newBufferedReader(CONFIG_PATH)) {
                properties.load(reader);
            } catch (IOException e) {
                PingOffsetMiner.LOGGER.error("Failed to load config", e);
            }
        } else {
            save();
        }
    }

    public void save() {
        try (var writer = Files.newBufferedWriter(CONFIG_PATH)) {
            properties.store(writer, "PingOffsetMiner configuration");
        } catch (IOException e) {
            PingOffsetMiner.LOGGER.error("Failed to save config", e);
        }
    }

    public boolean getActive() {
        return Boolean.parseBoolean(properties.getProperty("active", "true"));
    }

    public double getMiningSpeed() {
        return Double.parseDouble(properties.getProperty("miningSpeed", "-1.0"));
    }

    public double getPing() {
        return Double.parseDouble(properties.getProperty("ping", "-1.0"));
    }
}
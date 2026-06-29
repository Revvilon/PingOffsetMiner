package pom.rewrite.utility.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import pom.rewrite.PingOffsetMinerClient;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;

public class JsonUtil {
    private static final Gson GSON = new Gson();


    /**
     * Loads a JSON from the Assets folder using an Identifier
     * @param identifier e.g., Identifier.fromNamespaceAndPath("pom", "mining_data.json")
     */
    public static <T> List<T> loadFromAssets(Identifier identifier, TypeToken<List<T>> typeToken) {
        var resourceManager = Minecraft.getInstance().getResourceManager();

        Optional<Resource> resource = resourceManager.getResource(identifier);

        if (resource.isPresent()) {
            try (Reader reader = new InputStreamReader(resource.get().open())) {
                return GSON.fromJson(reader, typeToken.getType());
            } catch (Exception e) {
                PingOffsetMinerClient.LOGGER.error("Failed to load assets from {}", identifier, e);
                e.printStackTrace();
            }
        } else {
            PingOffsetMinerClient.LOGGER.error("Failed to find assets from {}", identifier);
        }
        return new ArrayList<>();
    }

    public static class DataRegistry<V> {
        private final Map<String, V> map = new LinkedHashMap<>();

        public void loadFromAssets(Identifier identifier, TypeToken<List<V>> typeToken, Function<V, List<String>> keyExtractor) {
            List<V> data = JsonUtil.loadFromAssets(identifier, typeToken);

            for (V entry : data) {
                List<String> rawName =  keyExtractor.apply(entry);
                if (rawName != null) {
                    for (String name : rawName) {
                        map.put(name, entry);
                    }
                }
            }
        }

        public Map<String, V> getMap() {return map;}
        public V get(String id) {
            return map.get(id);
        }
    }

    public static class MapRegistry {
        private final Map<String, Boolean> map = new LinkedHashMap<>();

        public void loadFromAssets(Identifier identifier) {
            TypeToken<Map<String, Boolean>> typeToken = new TypeToken<>() {};

            var resourceManager = Minecraft.getInstance().getResourceManager();
            Optional<Resource> resource = resourceManager.getResource(identifier);

            if (resource.isPresent()) {
                try (Reader reader = new InputStreamReader(resource.get().open())) {
                    Map<String, Boolean> rawMap = GSON.fromJson(reader, typeToken.getType());
                    if (rawMap != null) {
                        this.map.putAll(rawMap);
                    }
                } catch (Exception e) {
                    PingOffsetMinerClient.LOGGER.error("Failed to load assets from {}", identifier, e);
                    e.printStackTrace();
                }
            } else {
                PingOffsetMinerClient.LOGGER.error("Failed to find assets from {}", identifier);
            }
        }

        public Map<String, Boolean> getMap() {return map;}
    }
}

package pom.rewrite.utility.sound;

import com.google.common.reflect.TypeToken;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import pom.rewrite.PingOffsetMinerClient;
import pom.rewrite.utility.json.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SoundRegistryManager {
    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register((_) -> {
            loadAndStoreSounds();
        });
    }

    private static final List<SoundEvent> SOUNDS = new ArrayList<>();

    private static void loadAndStoreSounds() {
        SOUNDS.clear();

        Registry<SoundEvent> soundRegistry = BuiltInRegistries.SOUND_EVENT;

        for (SoundEvent soundEvent : soundRegistry) {
            SOUNDS.add(soundEvent);
        }

        PingOffsetMinerClient.LOGGER.info("Loaded {} sounds", SOUNDS.size());
    }

    public static List<String> getSounds() {
        return List.copyOf(SOUNDS).stream().map(event -> event.location().getPath()).toList();
    }
}

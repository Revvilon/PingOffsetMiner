package pom.rewrite.utility.server;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.resources.Identifier;
import pom.rewrite.features.debug.CustomStats;
import pom.rewrite.features.toggles.IslandToggles;
import pom.rewrite.utility.Util;
import pom.rewrite.utility.json.JsonUtil;

import java.util.Map;

import static pom.rewrite.PingOffsetMinerClient.MOD_ID;

public class IslandUtils {

    private static final Identifier jsonLocation = Identifier.fromNamespaceAndPath(MOD_ID, "data/islands.json");
    private static final JsonUtil.MapRegistry registry = new JsonUtil.MapRegistry();


    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> registry.loadFromAssets(jsonLocation));
    }

    public static Map<String, Boolean> getIslands() {
        return registry.getMap();
    }

    public static boolean isIsland() {
        if (CustomStats.instance.isEnabled()) return true;
        if (Util.getArea().isBlank()) return false;
        return IslandToggles.islands.isEnabled(Util.getArea());
    }
}

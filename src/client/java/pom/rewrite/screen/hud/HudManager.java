package pom.rewrite.screen.hud;

import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import pom.rewrite.screen.hud.elements.DebugHud;
import pom.rewrite.screen.hud.elements.EfficiencyHud;
import pom.rewrite.screen.hud.elements.ProfilerHud;
import pom.rewrite.screen.hud.elements.TickHud;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HudManager {

    private static final List<HudElement> TICKABLES = new CopyOnWriteArrayList<>();
    static List<HudElement> getTickables() {return TICKABLES;}

    public static void init() {
        Hud.add(TickHud.ID, () -> {
            TickHud tickHud = new TickHud();
            TICKABLES.add(tickHud);
            return tickHud;
        });
        Hud.add(EfficiencyHud.ID, () -> {
            EfficiencyHud eHud = new EfficiencyHud();
            TICKABLES.add(eHud);
            return eHud;
        });
        Hud.add(DebugHud.ID, () -> {
            DebugHud debugHud = new DebugHud();
            TICKABLES.add(debugHud);
            return debugHud;
        });
        Hud.add(ProfilerHud.ID, () -> {
            ProfilerHud pHud = new ProfilerHud();
            TICKABLES.add(pHud);
            return pHud;
        });
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            if (mc.level != null && mc.player != null) {
                for (HudElement tickable : TICKABLES) {
                    tickable.tick();
                }
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> clear());
    }

    public static void clear() {
        TICKABLES.clear();
    }
}

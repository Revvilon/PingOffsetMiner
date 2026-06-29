package pom.v1.gui;


import io.wispforest.owo.ui.hud.Hud;
import pom.v1.gui.hud.PerformanceStats;
import pom.v1.gui.hud.StatsDisplay;
import pom.v1.gui.hud.TickDisplay;

public class PomScreen {

    public static void initialize() {
        Hud.add(PerformanceStats.ID, PerformanceStats::new);
        Hud.add(TickDisplay.ID, TickDisplay::new);
        Hud.add(StatsDisplay.ID, StatsDisplay::new);
    }
}

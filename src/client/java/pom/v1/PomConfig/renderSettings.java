package pom.v1.PomConfig;

import java.awt.*;

public class renderSettings {
    public boolean active;
    public Color c1;
    public Color c2;
    public double width;

    public renderSettings(boolean active, Color c1, Color c2, double width) {
        this.active = active;
        this.c1 = c1;
        this.c2 = c2;
        this.width = width;
    }

    public static renderSettings lines() {
        return new renderSettings(true, Color.red, Color.green, 5.0);
    }

    public static renderSettings highlight() {
        return new renderSettings(false, new Color(255, 0, 0, 50), new Color(0, 255, 0, 50), 0);
    }
}

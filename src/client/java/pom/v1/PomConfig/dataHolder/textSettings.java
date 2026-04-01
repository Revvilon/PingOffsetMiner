package pom.v1.PomConfig.dataHolder;

import net.minecraft.client.Minecraft;
import pom.v1.PomConfig.PomConfig;

import java.awt.*;

public class textSettings {
    public PomConfig.Property<Boolean> active;
    public PomConfig.Property<Color> c1;
    public PomConfig.Property<Color> c2;
    public PomConfig.Property<Integer> x;
    public PomConfig.Property<Integer> y;
    public PomConfig.Property<Float> size;

    public textSettings(
            PomConfig.Property<Boolean> active,
            PomConfig.Property<Color> c1,
            PomConfig.Property<Color> c2,
            PomConfig.Property<Integer> x,
            PomConfig.Property<Integer> y,
            PomConfig.Property<Float> size) {
        this.active = active;
        this.c1 = c1;
        this.c2 = c2;
        this.y = y;
        this.x = x;
        this.size = size;
    }

    public static textSettings tickDisplay() {
        return new textSettings(
                new PomConfig.Property<> (false),
                new PomConfig.Property<> (new Color(255, 0, 0, 255)),
                new PomConfig.Property<> (new Color(0, 255, 0, 255)),
                new PomConfig.Property<> (50),
                new PomConfig.Property<> (48),
                new PomConfig.Property<> (1.0f)
        );
    }
    public static textSettings efficiencyDisplay() {
        return new textSettings(
                new PomConfig.Property<>(false),
                new PomConfig.Property<>(Color.red),
                new PomConfig.Property<>(Color.red),
                new PomConfig.Property<>(10),
                new PomConfig.Property<>(50),
                new PomConfig.Property<>(1.0f)
        );
    }
}

package pom.v1.PomConfig.dataHolder;

import pom.v1.PomConfig.PomConfig;

import java.awt.*;
import java.util.Properties;

public class renderSettings {
    public PomConfig.Property<Boolean> active;
    public PomConfig.Property<Color> c1;
    public PomConfig.Property<Color> c2;
    public PomConfig.Property<Double> width;
    public PomConfig.Property<Boolean> depth;

    public renderSettings(PomConfig.Property<Boolean> active,
                          PomConfig.Property<Color> c1,
                          PomConfig.Property<Color> c2,
                          PomConfig.Property<Double> width,
                          PomConfig.Property<Boolean> depth) {
        this.active = active;
        this.c1 = c1;
        this.c2 = c2;
        this.width = width;
        this.depth = depth;
    }

    public static renderSettings lines() {
        return new renderSettings(
                new PomConfig.Property<> (true),
                new PomConfig.Property<>(Color.red),
                new PomConfig.Property<>(Color.green),
                new PomConfig.Property<>(2.0),
                new PomConfig.Property<>(false));
    }

    public static renderSettings highlight() {
        return new renderSettings(
                new PomConfig.Property<>(true),
                new PomConfig.Property<>(new Color(255, 0, 0, 50)),
                new PomConfig.Property<>(new Color(0, 255, 0, 50)),
                new PomConfig.Property<>(2.0),
                new PomConfig.Property<>(false));
    }
}
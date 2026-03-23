package pom.v1.pomGetter;

import java.util.HashMap;

public class PomIslandData {
    private static final HashMap<String, Boolean> islands = new  HashMap<>() {{
        put("Private Island", false);
        put("Garden", false);
        put("Hub", false);
        put("The Barn", false);
        put("Mushroom Desert", false);
        put("The Park", false);
        put("Spider's Den", false);
        put("The End", false);
        put("Crimson Isle", false);
        put("Gold Mine", true);
        put("Dwarven Mines", true);
        put("Crystal Hollows", true);
        put("Jerry's Workshop", false);
        put("Dungeon Hub", false);
        put("Rift Dimension", false);
        put("Backwater Bayou", false);
        put("Mineshaft", true);
    }};

    public static HashMap<String, Boolean> getIslands() {
        return islands;
    }
}

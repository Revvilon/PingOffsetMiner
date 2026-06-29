package pom.v1.pomGetter;

import java.util.HashMap;
import java.util.Map;

public class PomIslandData {
    public static Map<String, Boolean> getIslands() {
       Map<String, Boolean> islands = new HashMap<>();
       islands.put("Private Island", false);
       islands.put("Garden", false);
       islands.put("Hub", false);
       islands.put("The Barn", false);
       islands.put("Mushroom Desert", false);
       islands.put("The Park", false);
       islands.put("Crystal Hollows", true);
       islands.put("Spider's Den", false);
       islands.put("The End", false);
       islands.put("Crimson Isle", false);
       islands.put("Gold Mine", true);
       islands.put("Dwarven Mines", true);
       islands.put("Jerry's Workshop", false);
       islands.put("Dungeon Hub", false);
       islands.put("Rift Dimension", false);
       islands.put("Backwater Bayou", false);
       islands.put("Mineshaft", true);
       islands.put("Deep Caverns", true);
         return islands;
    }
}

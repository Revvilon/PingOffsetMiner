package pom.v1;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import pom.v1.PomConfig.PomConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pom.v1.PingOffsetMinerClient.TOOL_STATS;
import static pom.v1.PomConfig.PomConfig.Config;

public class Util {

    public static void sendMsg(MutableComponent message) {
        Minecraft mc = Minecraft.getInstance();

        Component text = Component.literal("")
                .append(Component.literal("[").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("POM").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("] ").withStyle(ChatFormatting.WHITE))
                .append(message);

        if (mc.player == null) return;

        mc.player.displayClientMessage(text,false);

    }

    public static Boolean getIsland() {
        if (PomConfig.Config().debug) return true;

        if (Config().islandEnabled.getOrDefault(getArea(), false)) return true;

        return false;
    }

    public static String getArea() {
        for (String entry : Util.getTabList()) {
            String line = entry.replaceFirst("^Area: ", "");
            if (PomConfig.Config().islandEnabled.containsKey(line)) {

                return line;
            }
        }
        return "";
    }

    public class PingTracker {
        public static long lastId = -1;
        public static long sentTime = 0;
        public static int lastPing = 0;
    }

    public static double getAverage(int sampleCount) {
        return (int) PingTracker.lastPing;
    }

    public static double tps = 20;


    public static boolean updateTab = true;

    public static void updateTab() {
        updateTab = true;
    }
    private static ArrayList<String> tabList = new ArrayList<>();

    public static List<String> getTabList() {
        if (updateTab) {
            updateTab = false;
            tabList.clear();
            var network = Minecraft.getInstance().getConnection();
            if (network != null) {
                for (PlayerInfo entry : network.getOnlinePlayers()) {
                    if (entry.getTabListDisplayName() == null) continue;
                    String name = entry.getTabListDisplayName().getString();
                    name = name.replaceAll("$.", "");
                    tabList.add(name);
                }
            }
        }
        return tabList;
    }

    public static boolean foundSpeed() {
        for (String entry : Util.getTabList()) {
            if (convertSpeed(entry) > -1) return true;
        }
        return false;
    }

    public static double convertSpeed(String entry) {
        Pattern pattern = Pattern.compile("Mining Speed: \\uE015([0-9.]+)");
        Matcher matcher = pattern.matcher(entry);

        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static boolean shouldRender() {
        if (Config().ability) {
            switch (Config().msbToggleValue) {
                case OFF -> {
                    return !TOOL_STATS.getBoost();
                }
                case ON -> {
                    return TOOL_STATS.getBoost();
                }
            }
        }
        return true;
    }

    static HashMap<String, Long> logs = new HashMap<String, Long>();
    public static void log(String text, Long time) {
        if (Config().logging) return;
        logs.putIfAbsent(text, 10001L);
        if ((System.currentTimeMillis() - logs.get(text)) <= 2000) return;
        Util.sendMsg(Component.literal(text).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        logs.replace(text, time);


    }
}

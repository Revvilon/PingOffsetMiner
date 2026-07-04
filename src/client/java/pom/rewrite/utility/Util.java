package pom.rewrite.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import pom.rewrite.features.debug.Logging;
import pom.rewrite.utility.block.BlockObject;
import pom.rewrite.utility.server.ServerStats;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static <T, R> R getFromList(
            List<T> list,
            Function<T, String> extractor,
            Pattern pattern,
            Function<String, R> reader
    ) {
        if (list == null || list.isEmpty()) return  null;

        for (T entry : list) {
            if (entry == null) continue;

            String rawText = extractor.apply(entry);
            if (rawText == null) continue;

            String cleanLine = rawText.replaceAll("(?i)§.", "");
            Matcher matcher = pattern.matcher(cleanLine);

            if  (matcher.find()) {
                try {
                    return reader.apply(matcher.group(1));
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }


    static HashMap<String, Long> logs = new HashMap<>();
    public static void sendLog(String text) {
        if (!Logging.instance.isEnabled()) return;
        logs.putIfAbsent(text, 10001L);
        if ((System.currentTimeMillis() - logs.get(text)) <= 2000) return;
        Util.sendMsg(Component.literal(text).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        logs.replace(text, System.currentTimeMillis());
    }

    public static void sendMsg(Component message) {
        Component prettyMsg = Component.empty()
                .append(Component.literal("[").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                .append(Component.literal("POM").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
                .append(Component.literal("] ").withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD))
                .append(message);

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(prettyMsg);
        }
    }

    public static List<String> getTabList() {
        if (Minecraft.getInstance().getConnection() == null) return new ArrayList<>();

        return Minecraft.getInstance().getConnection().getListedOnlinePlayers().stream()
                .map(PlayerInfo::getTabListDisplayName)
                .filter(Objects::nonNull)
                .map(Component::getString)
                .map(s -> s.replaceAll("(?i)§.", ""))
                .toList();
    }


    public static String getArea() {
        for (String entry : Util.getTabList()) {
            if (entry.startsWith("Area: ")) {
                return entry.substring(6);
            }
        }
        return "";
    }

    public static int getTicksNeeded(int hardness, int miningSpeed) {
        if (miningSpeed <= 0) return 0;
        if (miningSpeed >= hardness * 30) return 0;

        double rawTicksToBreak = (double) (hardness * 30) / miningSpeed;

        double debugTps = ServerStats.getTps();
        double pingSec = ServerStats.getPing() / 1000.0;

        double pingMath = rawTicksToBreak - pingSec * debugTps;

        double pingOffset = rawTicksToBreak - pingMath > pingMath
                ? rawTicksToBreak - pingMath
                : rawTicksToBreak;

        return (int) Math.max(4, pingOffset);

        /*

        if (miningSpeed >= hardness * 30) return 0;

        double rawTicksToBreak = (double) (hardness * 30) / miningSpeed;
        int ticksToBreak = (int) Math.ceil(rawTicksToBreak);

        long ping = ServerStats.getPing();
        double tps = ServerStats.getTps();
        double pingInTicks = (ping * tps) / 1000.0;
        pingInTicks = Math.max(0.0, pingInTicks);

        double pingOffset = Math.min(pingInTicks, ticksToBreak - 1);
        double totalTicks = (double) ticksToBreak - pingOffset;

        return (int) Math.max(4, Math.ceil(totalTicks));
    }

    public static int getTicksNeeded(BlockObject block, int miningSpeed) {
        return getTicksNeeded(block.hardness, miningSpeed);
    }

    public static int getTicksNeededNoOffset(int hardness, int miningSpeed) {
        return Math.max(4, (hardness * 30) / miningSpeed);
    }

    public static int getTicksNeededNoOffset(BlockObject block, int miningSpeed) {
        return getTicksNeededNoOffset(block.hardness, miningSpeed);
    }

    public static int getTotalMined(Map<String, Integer> map) {
        int count = 0;
        for(Integer i : map.values()) {
            count += i;
        }
        return count;
    }

}
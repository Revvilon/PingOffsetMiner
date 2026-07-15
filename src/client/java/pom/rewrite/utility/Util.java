package pom.rewrite.utility;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import pom.rewrite.PingOffsetMinerClient;
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

    public static int getTicksNeededProgress(int hardness, int miningSpeed, int ticksElapsed, double progressMade) {
        if (miningSpeed <= 0) return 0;
        if (miningSpeed >= hardness * 30 && ticksElapsed == 0) return 0;

        double remainingProgress = Math.max(0.0, (hardness * 30.0) - progressMade);
        double rawTicksNeeded = ticksElapsed + remainingProgress / miningSpeed;

        double debugTps = ServerStats.getTps();
        double pingSec = ServerStats.getPing() / 1000.0;

        double pingMath = rawTicksNeeded - pingSec * debugTps;

        double pingOffset = rawTicksNeeded - pingMath > pingMath
                ? rawTicksNeeded - pingMath
                : rawTicksNeeded;

        return (int) Math.max(4, pingOffset);
    }

    public static int getTicksNeeded(int hardness, int miningSpeed) {
        return getTicksNeededProgress(hardness, miningSpeed, 0, 0.0);
    }

    public static int getTicksNeeded(BlockObject block, int miningSpeed) {
        return getTicksNeeded(block.hardness, miningSpeed);
    }

    public static int getTicksNeededNoOffset(int hardness, int miningSpeed) {
        if (miningSpeed <= 0) return 0;
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

    public static boolean isSoundEvent(String identifier) {
        return BuiltInRegistries.SOUND_EVENT.getOptional(Identifier.parse(identifier)).isPresent();
    }

    public static boolean compareComponents(DataComponentMap component1, DataComponentMap component2) {
        byte x = 0;

        for (TypedDataComponent<?> i : component1) {
            DataComponentType<?> type = i.type();

            if (!component2.has(type)) {
                continue;
            }

            Object value2 = component2.get(type);
            String val1String = String.valueOf(i.value());
            String val2String = String.valueOf(value2);

            if (!Objects.equals(val1String, val2String)) {
                String tagName = type.toString();

                if (tagName.equals("minecraft:damage") || tagName.equals("minecraft:lore")) {
                    x |= 2;
                } else if (tagName.equals("minecraft:custom_data")) {
                    if (compareUUIDS(val1String, val2String)) {
                        x |= 4;
                    } else {
                        x |= 1;
                    }
                }
            }
        }
        return ((x & 2) == 2 || (x & 4) == 4) && (x & 1) == 0;
    }

    public static boolean compareUUIDS(String val1, String val2) {
        try {
            String uuid1 = getUUID(val1);
            String uuid2 = getUUID(val2);
            return uuid1 != null && uuid1.equals(uuid2);
        } catch (Exception e) {
            PingOffsetMinerClient.LOGGER.error("Error comparing UUIDS");
            return false;
        }
    }

    private static String getUUID(String str) {
        if (str == null) return null;

        int uuidIndex = str.indexOf("uuid:");
        if (uuidIndex != -1) {
            int firstQuote = str.indexOf("\"", uuidIndex);
            if (firstQuote != -1) {
                int secondQuote = str.indexOf("\"", firstQuote + 1);
                if (secondQuote != -1) {
                    String sub = str.substring(firstQuote + 1, secondQuote);
                    if (sub.length() == 36) {
                        return sub;
                    }
                }
            }
        }
        return str;
    }
}
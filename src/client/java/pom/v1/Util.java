package pom.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import pom.v1.modmenu.pomConfig;

public class Util {

    public static void sendMsg(String message, Formatting type) {
        MinecraftClient mc = MinecraftClient.getInstance();

        Text text = Text.literal("")
                .append(Text.literal("[").formatted(Formatting.WHITE))
                .append(Text.literal("POM").formatted(Formatting.AQUA))
                .append(Text.literal("] ").formatted(Formatting.WHITE))
                .append(Text.literal(message).formatted(type));

        if (mc.player == null) return;

        mc.player.sendMessage(text,false);

    }

    public static Boolean getIsland() {
        var network = MinecraftClient.getInstance().getNetworkHandler();
        if (network == null) return false;

        for (PlayerListEntry entry : network.getPlayerList()) {
            if (entry.getDisplayName() == null) continue;
            String text = entry.getDisplayName().getString();

            if (text.contains("Dwarven Mines") || text.contains("Crystal Hollows") || text.contains("Mineshaft")) {
                return true;
            }
        }

        return false;
    }

    public static double speed() {
        var network = MinecraftClient.getInstance().getNetworkHandler();
        if (!pomConfig.HANDLER.instance().active)  {
            return -1;
        }

        if (pomConfig.HANDLER.instance().debug) return pomConfig.HANDLER.instance().speed;

        if (network == null) return -1;
        for (PlayerListEntry entry : network.getPlayerList()) {
            if (entry.getDisplayName() == null) continue;

            String text = entry.getDisplayName().getString();

            if (text.contains("Mining Speed:") || text.contains("⸕")) {
                String cleaned = text.replaceAll("[^0-9]", "").replace("⸕", "");
                if (!cleaned.isEmpty()) {
                    return Double.parseDouble(cleaned);
                }
            }
        }
        return -1;

    }

    public static double getAverage(int sampleCount) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud == null) return 0;

        MultiValueDebugSampleLogImpl log;

        DebugHud debugHud = (client.inGameHud.getDebugHud());
        log = debugHud.getPingLog();


        if (log == null) return 0;

        int availableEntries = Math.min(sampleCount, log.getLength());
        if (availableEntries <= 0) return 0;

        long total = 0;
        int count = 0;

        for (int i = 0; i < availableEntries; i++) {
            long sample = log.get(i, 0);

            if (sample > 0) {
                total += sample;
                count++;
            }
        }
        return count > 0 ? (double) total / count : 0;
    }

    public static double tps = 20;
}

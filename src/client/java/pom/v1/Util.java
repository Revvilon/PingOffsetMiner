package pom.v1;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.debugchart.SampleStorage;
import pom.v1.modmenu.pomConfig;

public class Util {

    public static pomConfig Config = pomConfig.HANDLER.instance();

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
        if (Config.debug) return true;
        var network = Minecraft.getInstance().getConnection();
        if (network == null) return false;

        for (PlayerInfo entry : network.getOnlinePlayers()) {
            if (entry.getTabListDisplayName() == null) continue;
            String text = entry.getTabListDisplayName().getString();

            if (text.contains("Dwarven Mines") || text.contains("Crystal Hollows") || text.contains("Mineshaft")) {
                return true;
            }
        }

        return false;
    }

    public static double previousSpeed = -1;

    public static double speed() {
        if (!Config.active)  {
            return -1;
        }

        if (Config.debug) return Config.speed;

        return previousSpeed;
    }

    public static boolean boost = true;

    public static double getAverage(int sampleCount) {
        Minecraft client = Minecraft.getInstance();

        SampleStorage log;

        DebugScreenOverlay debugHud = (client.gui.getDebugOverlay());
        log = debugHud.getPingLogger();


        int availableEntries = Math.min(sampleCount, log.size());
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

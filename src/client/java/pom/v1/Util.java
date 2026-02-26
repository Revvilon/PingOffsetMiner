package pom.v1;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.debugchart.SampleStorage;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import pom.v1.modmenu.pomConfig;

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

    public static double speed() {
        var network = Minecraft.getInstance().getConnection();
        if (!pomConfig.HANDLER.instance().active)  {
            return -1;
        }

        if (pomConfig.HANDLER.instance().debug) return pomConfig.HANDLER.instance().speed;

        if (network == null) return -1;
        for (PlayerInfo entry : network.getOnlinePlayers()) {
            if (entry.getTabListDisplayName() == null) continue;

            String text = entry.getTabListDisplayName().getString();

            if (text.contains("Mining Speed:") && text.contains("⸕")) {
                String cleaned = text.replaceAll("[^0-9]", "").replace("⸕", "");
                if (!cleaned.isEmpty()) {
                    return Double.parseDouble(cleaned);
                }
            }
        }
        return -1;

    }
    public static boolean boost() {
        var network = Minecraft.getInstance().getConnection();
        if (network == null || !pomConfig.HANDLER.instance().ability) return false;
       for (PlayerInfo entry : network.getOnlinePlayers()) {
            if (entry.getTabListDisplayName() == null) continue;

            String text = entry.getTabListDisplayName().getString();

            if (text.contains("Mining Speed Boost:") && text.contains("Available")) {
                return false;
            } else if (text.contains("Mining Speed Boost:")) {
                return true;
           }
        }
        return false;
    }
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


    public static BlockPos blockPos(Minecraft mc) {
        if (mc.level == null || mc.player == null) return null;

        HitResult hr = mc.hitResult;

        if (hr != null && hr.getType() ==  HitResult.Type.BLOCK) {
            return ((BlockHitResult) hr).getBlockPos();
        }

        return  null;
    }
}

package me.bubner.pingoffsetminer.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;

public class Util {
    /**
     * Send a formatted message to the player.
     */
    public static void sendMsg(String message) {
        Component text = Component.literal("")
                .append(Component.literal("[").withStyle(ChatFormatting.DARK_RED))
                .append(Component.literal("POM").withStyle(ChatFormatting.RED))
                .append(Component.literal("] ").withStyle(ChatFormatting.DARK_RED))
                .append(Component.literal(message).withStyle(ChatFormatting.GRAY));

        Minecraft.getInstance().gui.getChat().addMessage(text);
    }

    /**
     * Check if the user is in SkyBlock by analysing the scoreboard.
     */
    public static boolean isInSkyblock() {
        try {
            if (Minecraft.getInstance().level == null) return false;
            Objective obj = Minecraft.getInstance().level.getScoreboard().getDisplayObjective(DisplaySlot.SIDEBAR);
            return obj != null && obj.getDisplayName().getString().contains("SKYBLOCK");
        } catch (NullPointerException e) {
            return false;
        }
    }
}

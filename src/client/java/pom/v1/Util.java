package pom.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Util {

    public static void sendMsg(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();

        Text text = Text.literal("")
                .append(Text.literal("[").formatted(Formatting.WHITE))
                .append(Text.literal("POM").formatted(Formatting.AQUA))
                .append(Text.literal("] ").formatted(Formatting.WHITE))
                .append(Text.literal(message).formatted(Formatting.GRAY));

        assert mc.player != null;
        mc.player.sendMessage(text,false);
    }

    public static boolean isInSkyblock() {
        try {
            if (MinecraftClient.getInstance().world == null) return false;
            ScoreboardObjective obj = MinecraftClient.getInstance().world.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            return obj != null && obj.getDisplayName().getString().contains("SKYBLOCK");
        } catch (NullPointerException e) {
            return false;
        }
    }
}

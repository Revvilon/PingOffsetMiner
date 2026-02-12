package pom.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
}

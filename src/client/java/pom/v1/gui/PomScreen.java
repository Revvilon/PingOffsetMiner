package pom.v1.gui;


import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import pom.v1.Util;

import java.util.ArrayList;
import java.util.List;

import static pom.v1.PingOffsetMinerClient.*;
import static pom.v1.PomConfig.PomConfig.Config;

public class PomScreen {

    public static void render(GuiGraphics context, DeltaTracker delta) {

        if (!Config().active.get() || !Util.getIsland()) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;


        int screenWidth = mc.getWindow().getGuiScaledWidth(), screenHeight = mc.getWindow().getGuiScaledHeight();

        int centerX = screenWidth / 2, centerY = screenHeight / 2;

        double basisX = (screenWidth / 2.0);
        double basisY = (screenHeight / 2.0);

        renderTracker(context, centerX, centerY, (int) basisX, (int) basisY, font);
        renderTicks(context, centerX, centerY, (int) basisX, (int) basisY, font);


    }

    public static void renderTracker(GuiGraphics context, int centerX, int centerY, int basisX, int basisY, Font font) {
        if (!Config().efficiencyDisplay.active.get()) return;
        List<MutableComponent> list = getTracker();

        double normalizedX = ((Config().efficiencyDisplay.x.get() / 100.0) * 2.0) - 1.0;
        double normalizedY = ((Config().efficiencyDisplay.y.get() / 100.0) * 2.0) - 1.0;

        int totalHeight = list.size() * font.lineHeight;

        for (int i = 0; i < list.size(); i++) {
            Component line = list.get(i);

            int finalX = (int) (centerX + (normalizedX * basisX));
            int finalY = (int) (centerY + (normalizedY * basisY) - ((totalHeight / 2.0)) + (i * font.lineHeight));

            context.drawString(font, line, finalX, finalY, -1);
        }
    }

    public static void renderTicks(GuiGraphics context, int centerX, int centerY, int basisX, int basisY, Font font) {
        if (!Config().tickDisplay.active.get() || POM_BLOCK.isEmpty()) return;

        int color = POM_CALC.timeoutExceeded() ? Config().tickDisplay.c2.get().getRGB() : Config().tickDisplay.c1.get().getRGB();

        double normalizedX = ((Config().tickDisplay.x.get() / 100.0) * 2.0) - 1.0;
        double normalizedY = ((Config().tickDisplay.y.get() / 100.0) * 2.0) - 1.0;

        String text = POM_CALC.getTicksElapsed() + "/" + POM_CALC.getTicksNeeded();

        int finalX = (int) (centerX + (normalizedX * basisX) - (font.width(text) / 2.0));
        int finalY = (int) (centerY + (normalizedY * basisY) - (font.lineHeight / 2.0));

        context.drawString(font, text, finalX, finalY, color);

    }

    public static List<MutableComponent> getTracker() {
        List<MutableComponent> list = new ArrayList<>();

                list.add(Component.literal("Uptime: ").withStyle(ChatFormatting.AQUA)
                                .append(Component.literal(String.format("%.1f", POM_EFF.getUpTime())).withStyle(ChatFormatting.WHITE))
                                .append(Component.literal("s").withStyle(ChatFormatting.AQUA)));

                list.add(Component.literal("Efficiency: ").withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(String.format("%.1f", POM_EFF.getTempEff() * 100)).withStyle(ChatFormatting.WHITE))
                        .append(Component.literal("%")));


                if (Config().debugGui.get()) {
                    list.add(Component.literal("Is mining: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_EFF.isMining() + "").withStyle(ChatFormatting.WHITE)));

                    list.add(Component.literal("Blocks mined: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.getTotalMined(POM_BLOCK.getMinedBlocks()) + "").withStyle(ChatFormatting.WHITE)));


                    list.add(Component.literal("Theoretical max: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.getTotalMined(POM_EFF.getMaxBlocksMined()) +  "").withStyle(ChatFormatting.WHITE)));

                    list.add(Component.literal("Latest block hit: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.getLastBlockHit()).withStyle(ChatFormatting.WHITE)));

                    list.add(Component.literal("Latest block mined: ").withStyle(ChatFormatting.AQUA)
                            .append(Component.literal(POM_BLOCK.lastMinedBlock).withStyle(ChatFormatting.WHITE)));
                }
        return list;
    }
}

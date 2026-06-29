package pom.v1.pomGetter;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import pom.v1.Util;
import pom.v1.events.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pom.v1.PingOffsetMinerClient.TOOL_STATS;
import static pom.v1.PomConfig.PomConfig.Config;

public class PomStats {

    /*
    ||||
    ||||
    |||| The code below is heavily inspired by NoFrills, a great skyblock mod
    |||| Everything written here is mostly using the same logic that it uses,
    |||| but changed to fit my own needs.
    ||||
    ||||
     */

    private List<String> getToolTip(ItemStack stack) {
        LocalPlayer player = Minecraft.getInstance().player;
        List<String> tooltip = new ArrayList<>();
        if (player != null) {
            Item.TooltipContext context = Item.TooltipContext.of(player.registryAccess());
            TooltipFlag flag = TooltipFlag.ADVANCED;

            List<Component> toolTipLines = stack.getTooltipLines(context, player, flag);

            for (Component line : toolTipLines) {
                String text = line.getString();
                tooltip.add(text);
            }
            return tooltip;
        }
        return tooltip;
    }

    private boolean isTool(ItemStack stack) {
        if (!stack.isEmpty()) {
            for (String entry : getToolTip(stack).reversed()) {
                if (!entry.isEmpty()) {
                    if (entry.contains("DRILL") || entry.contains("GAUNTLET") || entry.contains("PICKAXE")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    final Pattern pattern2 = Pattern.compile("\\s*([0-9.]+)\\s*s");
    final Pattern pattern1 = Pattern.compile("\\+([0-9.]+)%");

    private int getCooldown(ItemStack stack) {

        String line = getLine(stack, pattern2);
        if (line == null) {
            return -1;
        }
        return Integer.parseInt(line);
    }

    private double getMSB(ItemStack stack) {
        String line = getLine(stack, pattern1);
        if (line == null) {
            return -1;
        }
        return (1+ (Double.parseDouble(line) / 100));
    }

    private String getLine(ItemStack stack, Pattern pattern) {
        boolean found = false;

        for (String entry : getToolTip(stack)) {
            String clean = entry.replaceAll("(?i)§.", "");

            if (!found && clean.contains("Ability: Mining Speed")) {
                found = true;
                continue;
            }
            if (found) {
                Matcher matcher = pattern.matcher(clean);

                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return null;
    }


    public class MiningStats {

        private ItemStack item = ItemStack.EMPTY;
        private double speed = -1;
        private boolean boost = false;
        private int cd = -1;
        private double msb = 0;

        public void setItem(ItemStack newItem) {
            if (newItem == this.item) return;
            if (isTool(newItem)) {
                this.item = newItem;
                this.cd = getCooldown(newItem);
                this.msb = getMSB(newItem);
            } else {
                this.item = ItemStack.EMPTY;
                this.cd = -1;
            }
        }

        public void setSpeed(double speed) {
            if (this.speed == speed) return;

            this.speed = speed;
        }

        public boolean isActive() {
            return Config().debug.get() || !this.item.isEmpty();
        }

        public void setBoost(boolean boost) {
            this.boost = boost;
        }

        public double getMsb() {
            return msb;
        }
        public int getCd() { return this.cd; }
        public double getSpeed() { return this.speed; }
        public boolean getBoost() { return this.boost; }
    }

    @EventHandler
    public void onHeldSlot(onHeldSlot event) {
        TOOL_STATS.setItem(event.held);
    }

    @EventHandler
    public void onSpeedUpdate(onSpeedUpdate event) {
        if (TOOL_STATS.isActive() && Util.shouldRender()) {
            TOOL_STATS.setSpeed(event.speed);
        }
    }

    private static final Pattern BOOST_PATTERN = Pattern.compile(
            "(?i)you used your mining speed boost pickaxe ability!"
    );

    double speed = 0;

    @EventHandler
    public void onChatMessage(onChatMessage event) {
            String raw = event.message.replaceAll("$.", "");
            Matcher matcher = BOOST_PATTERN.matcher(raw);
            if (matcher.matches() && !TOOL_STATS.getBoost()) {
                TOOL_STATS.setBoost(true);
                TOOL_STATS.setSpeed(TOOL_STATS.getSpeed() * TOOL_STATS.getMsb());
                Util.log("MSB enabled!", System.currentTimeMillis());
            }
    }
    int tickCount = 0;
    @EventHandler
    public void onTick(worldTickEvent event) {
        if (TOOL_STATS.getBoost()) {
                tickCount++;

            if (tickCount >= (TOOL_STATS.getCd() * 20)) {
                    tickCount = 0;
                    TOOL_STATS.setBoost(false);
                    TOOL_STATS.setSpeed(TOOL_STATS.getSpeed() / TOOL_STATS.getMsb());
                    Util.log("MSB disabled!", System.currentTimeMillis());
                }
        }
    }

    @EventHandler
    public void joined(gameJoinedEvent event) {
        if (!TOOL_STATS.getBoost()) return;
        for (String entry : Util.getTabList()) {
            if (entry.contains("speed boost: available!")) {
                tickCount = 0;

                TOOL_STATS.setBoost(false);
                Util.log("MSB disabled!", System.currentTimeMillis());
            }
        }
    }
}

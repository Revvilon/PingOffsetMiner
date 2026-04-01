package pom.v1.pomGetter;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import pom.v1.Util;
import pom.v1.events.onChatMessage;
import pom.v1.events.onHeldSlot;
import pom.v1.events.onSpeedUpdate;
import pom.v1.events.worldTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pom.v1.PingOffsetMinerClient.*;
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

    private int getCooldown(ItemStack stack) {
        if (!Config().ability.get()) return -1;
        Pattern pattern = Pattern.compile("\\s*([0-9.]+)\\s*s");

        boolean found = false;

        for (String entry : getToolTip(stack)) {
            String clean = entry.replaceAll("$.", "");
            Matcher matcher = pattern.matcher(clean);

            if (!found && clean.contains("Ability: Mining Speed")) {
                found = true;
                continue;
            }

            if (found) {
                if (matcher.find()) {
                    try {
                        return (int) (Double.parseDouble(matcher.group(1)));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return -1;
    }


    public class MiningStats {

        private ItemStack item = ItemStack.EMPTY;
        private double speed = -1;
        private boolean boost = false;
        private int cd = -1;

        public void setItem(ItemStack newItem) {
            if (newItem == this.item) return;
            if (isTool(newItem)) {
                this.item = newItem;
                this.cd = getCooldown(newItem);
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

    @EventHandler
    public void onChatMessage(onChatMessage event) {
        if (Config().ability.get()) {
            String raw = event.message.replaceAll("$.", "");
            Matcher matcher = BOOST_PATTERN.matcher(raw);
            if (matcher.matches()) {
                TOOL_STATS.setBoost(true);
                Util.log("MSB enabled!", System.currentTimeMillis());
            }
        }
    }
    int tickCount = 0;
    @EventHandler
    public void onTick(worldTickEvent event) {
        if (Config().ability.get() && TOOL_STATS.getBoost()) {
                tickCount++;

                for (String entry : Util.getTabList()) {
                    if (entry.contains("speed boost: available!")) {
                        tickCount = 0;

                        TOOL_STATS.setBoost(false);
                        Util.log("MSB disabled!", System.currentTimeMillis());
                    }
                }

                if (tickCount >= (TOOL_STATS.getCd() * 20*(20/getTPS()))) {
                    tickCount = 0;
                    TOOL_STATS.setBoost(false);
                    Util.log("MSB disabled!", System.currentTimeMillis());
                }
        }
    }
}

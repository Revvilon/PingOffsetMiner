package pom.v1.pomGetter;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import pom.v1.PomConfig.PomConfig;
import pom.v1.Util;
import pom.v1.events.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pom.v1.PingOffsetMinerClient.*;

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

    PomConfig Config = PomConfig.Config();

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
                    if (entry.contains(" DRILL ") || entry.contains(" GAUNTLET ") || entry.contains(" PICKAXE ")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getCooldown(ItemStack stack) {
        if (Config.ability) return -1;
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

    private double previousSpeed = -1;
    private double newSpeed = -1;

    public class MiningStats {

        public ItemStack item = ItemStack.EMPTY;
        public double speed = -1;
        public boolean boost = false;
        public double ticks = -1;
        public int cd = -1;

        public void setItem(ItemStack newItem) {
            if (newItem != this.item) {
                if (isTool(newItem)) {
                    this.item = newItem;
                    this.cd = getCooldown(newItem);
                } else  {
                    this.item = ItemStack.EMPTY;
                }
            }
        }
        public ItemStack getItem() {
            return this.item;
        }

        public int getCd() {
            return this.cd;
        }

        public void setSpeed(double speed) {
            this.ticks = SpeedCalc.getTicksToBreak((int) POM_BLOCK.getHardness(), speed);
            this.speed = speed;
        }
        public double getSpeed() {
            return this.speed;
        }

        public boolean isActive() {
            if (PomConfig.Config().debug) return true;
            return this.item != ItemStack.EMPTY;
        }


        public void setBoost(boolean boost) {
            this.boost = boost;
        }

        public boolean getBoost() {
            return this.boost;
        }
    }

    @EventHandler
    public void onHeldSlot(onHeldSlot event) {
        TOOL_STATS.setItem(event.held);
    }

    @EventHandler
    public void onSpeedUpdate(onSpeedUpdate event) {
        if (TOOL_STATS.isActive()) {
            TOOL_STATS.setSpeed(event.speed);
        }
    }

    private static final Pattern BOOST_PATTERN = Pattern.compile(
            "(?i)you used your mining speed boost pickaxe ability!"
    );

    @EventHandler
    public void onChatMessage(onChatMessage event) {
        if (Config.ability) {
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
        if (Config.ability) {
            if (TOOL_STATS.getBoost()) {
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
}

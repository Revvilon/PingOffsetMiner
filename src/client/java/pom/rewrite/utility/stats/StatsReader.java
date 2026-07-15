package pom.rewrite.utility.stats;

import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import pom.rewrite.events.*;
import pom.rewrite.utility.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatsReader {
    private static final StatsReader INSTANCE = new StatsReader();
    public static StatsReader getInstance() { return INSTANCE; }
    private StatsReader() {}

    private final MiningStats stats = MiningStats.instance();

    private List<String> getToolTip(ItemStack stack) {
        LocalPlayer player = Minecraft.getInstance().player;
        List<String> tooltip = new ArrayList<>();
        if (player == null) return tooltip;

        Item.TooltipContext context  = Item.TooltipContext.of(player.registryAccess());
        TooltipFlag flag = TooltipFlag.ADVANCED;

        List<Component> toolTipLines = stack.getTooltipLines(context, player, flag);

        for (Component line : toolTipLines) {
            tooltip.add(line.getString());
        }
        return tooltip;
    }

    private ItemStack asTool(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        for (String entry : getToolTip(stack).reversed()) {
            if (entry.isEmpty()) continue;
            if (entry.contains("DRILL") || entry.contains("GAUNTLET") || entry.contains("PICKAXE")) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private int getCooldown(ItemStack stack) {
        return getLine(stack, cooldownPattern)
                .map(line -> (int) (Float.parseFloat(line) * 20))
                .orElse(0);
    }
    private float getMsbMultiplier(ItemStack stack) {
        return getLine(stack, msbPattern)
                .map(line -> 1 + (Float.parseFloat(line) / 100))
                .orElse(1.0f);
    }

    private boolean isMsbFound(ItemStack stack) {
        return getToolTip(stack).stream()
                .anyMatch(line -> line.contains("Ability: Mining Speed"));
    }

    private final Pattern cooldownPattern = Pattern.compile("\\s*([0-9.]+)\\s*s");
    private final Pattern msbPattern = Pattern.compile("\\+([0-9.]+)%");

    private Optional<String> getLine(ItemStack stack, Pattern pattern) {
        List<String> tooltip = getToolTip(stack);

        int index = -1;
        for (int i = 0; i < tooltip.size(); i++) {
            if (tooltip.get(i).contains("Ability: Mining Speed")) {
                index = i;
                break;
            }
        }

        if (index == -1) return Optional.empty();

        return tooltip.subList(index + 1, tooltip.size()).stream()
                .map(line -> line.replaceAll("(?i)§.", ""))
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .findFirst();
    }


    private final Pattern miningSpeedPattern = Pattern.compile("Mining Speed: \\uE015([0-9.]+)");

    public boolean miningSpeedFound() {
        return Util.getFromList(Util.getTabList(), s -> s, miningSpeedPattern, Integer::parseInt) != null;
    }

    private void setSpeedFromList() {
        if (!stats.isActive()) return;

        Integer miningSpeed = Util.getFromList(Util.getTabList(), s -> s, miningSpeedPattern, Integer::parseInt);

        if (miningSpeed == null) return;
        stats.setMiningSpeed(miningSpeed);
    }

    @EventHandler
    public void tabUpdate(tabUpdate event) {
        setSpeedFromList();
    }
    @EventHandler
    public void heldSlotUpdate(heldSlotUpdate event) {
        ItemStack newItem = event.item;

        stats.setItem(asTool(newItem));

        if (stats.isActive() && isMsbFound(newItem)) {
            stats.setMsbCooldown(getCooldown(newItem));
            stats.setMsbMultiplier(getMsbMultiplier(newItem));
        }

    }

    private static final Pattern msbChatPattern = Pattern.compile(
            "(?i)you used your mining speed boost pickaxe ability!"
    );

    @EventHandler
    public void onChatMessage(chatEvent event) {
        String msg = event.message;

        Matcher matcher = msbChatPattern.matcher(msg);
        if (matcher.matches()) {
            stats.setMsbActive(true);
            stats.setMiningSpeed((int) (stats.getBaseMiningSpeed() * stats.getMsbMultiplier()));
        }
    }

    private int ticks = 0;
    @EventHandler
    public void onTick(clientTick tick) {
        if (!stats.getMsbActive()) return;

        ticks++;
        if (ticks >= stats.getMsbCooldown()) {
            ticks = 0;
            stats.setMsbActive(false);
            stats.setMiningSpeed((int) (stats.getBaseMiningSpeed() / stats.getMsbMultiplier()));
        }
    }

    @EventHandler
    public void worldJoin(gameJoined event) {
        if (!stats.getMsbActive()) return;

        for (String entry : Util.getTabList()) {
            if (entry.contains("speed boost: available")) {
                ticks = 0;
                stats.setMsbActive(false);
            }
        }
    }
}

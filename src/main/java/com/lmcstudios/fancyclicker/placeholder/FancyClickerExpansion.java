package com.lmcstudios.fancyclicker.placeholder;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import com.lmcstudios.fancyclicker.data.PlayerData;
import com.lmcstudios.fancyclicker.gui.ClickerGUI;
import com.lmcstudios.fancyclicker.util.NumberUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class FancyClickerExpansion extends PlaceholderExpansion {

    private final FancyClickerPlugin plugin;

    public FancyClickerExpansion(FancyClickerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fancyclicker";
    }

    @Override
    public @NotNull String getAuthor() {
        return "lmc Studios";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        PlayerData data = plugin.getDataManager().get(player);

        double rate = plugin.getConfig().getDouble("economy.cookies-per-money", 10.0);
        double moneyExact = rate > 0 ? data.getCookies() / rate : 0;
        long nextCost = plugin.getConfig().getLong("level-up.base-cost", 5000) * data.getLevel();

        return switch (params.toLowerCase()) {
            case "total" -> NumberUtil.format(data.getTotalCookies());
            case "currently" -> NumberUtil.format(data.getCookies());
            case "level" -> String.valueOf(data.getLevel());
            case "perclick" -> NumberUtil.format(ClickerGUI.clickValue(data.getLevel()));
            case "money" -> String.format(Locale.US, "%.2f", moneyExact);
            case "money_rounded" -> NumberUtil.format(Math.round(moneyExact));
            case "nextlevel" -> String.valueOf(data.getLevel() + 1);
            case "nextcost" -> NumberUtil.format(nextCost);
            case "preference" -> data.getPreference().name();
            case "preference_friendly" -> data.getPreference() == PlayerData.Preference.LEFT
                    ? "Linksklick" : "Rechtsklick";
            default -> null;
        };
    }
}

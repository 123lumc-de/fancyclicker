package com.lmcstudios.fancyclicker.placeholder;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import com.lmcstudios.fancyclicker.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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

        return switch (params.toLowerCase()) {
            case "total" -> String.valueOf(data.getTotalCookies());
            case "currently" -> String.valueOf(data.getCookies());
            case "level" -> String.valueOf(data.getLevel());
            case "perclick" -> String.valueOf(data.getLevel());
            default -> null;
        };
    }
}
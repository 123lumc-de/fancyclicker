package com.gerhartstudios.cookieclicker.placeholder;

import com.gerhartstudios.cookieclicker.CookieClickerPlugin;
import com.gerhartstudios.cookieclicker.data.PlayerData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class CookieClickerExpansion extends PlaceholderExpansion {

    private final CookieClickerPlugin plugin;

    public CookieClickerExpansion(CookieClickerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cookieclicker";
    }

    @Override
    public @NotNull String getAuthor() {
        return "GerhartStudios";
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
            default -> null;
        };
    }
}

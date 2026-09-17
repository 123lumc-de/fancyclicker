package com.lmcstudios.cookieclicker.listeners;

import com.lmcstudios.cookieclicker.CookieClickerPlugin;
import com.lmcstudios.cookieclicker.data.PlayerData;
import com.lmcstudios.cookieclicker.gui.ClickerGUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final CookieClickerPlugin plugin;
    private final ClickerGUI gui;
    private final Map<UUID, Inventory> openInventories = new HashMap<>();

    public GUIListener(CookieClickerPlugin plugin) {
        this.plugin = plugin;
        this.gui = new ClickerGUI(plugin);
    }

    public void open(Player player) {
        Inventory inv = gui.build(player);
        openInventories.put(player.getUniqueId(), inv);
        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ClickerGUI.CookieHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) return;

        PlayerData data = plugin.getDataManager().get(player);

        switch (slot) {
            case ClickerGUI.SLOT_COOKIE -> handleCookieClick(player, data, event.getInventory());
            case ClickerGUI.SLOT_LEVELUP -> handleLevelUp(player, data, event.getInventory());
            case ClickerGUI.SLOT_CASHOUT -> handleCashOut(player, data, event.getInventory());
            default -> {
                // Deko-Slot, nichts tun
            }
        }
    }

    private void handleCookieClick(Player player, PlayerData data, Inventory inv) {
        long value = ClickerGUI.clickValue(data.getLevel());
        data.addCookies(value);
        playSound(player, "click");
        gui.refresh(inv, player);
    }

    private void handleLevelUp(Player player, PlayerData data, Inventory inv) {
        long cost = gui.nextLevelCost(data.getLevel());
        if (!data.removeCookies(cost)) {
            player.sendMessage(color(plugin.getConfig().getString("messages.prefix", "")) +
                    color("&cDu hast nicht genug Cookies fuer das naechste Level."));
            return;
        }
        data.levelUp();
        playSound(player, "levelup");
        gui.refresh(inv, player);
    }

    private void handleCashOut(Player player, PlayerData data, Inventory inv) {
        if (data.getCookies() <= 0) {
            player.sendMessage(color(plugin.getConfig().getString("messages.prefix", "")) + color(plugin.msg("cashout-none")));
            return;
        }
        if (!plugin.getConfig().getBoolean("economy.enabled", true) || !plugin.getEconomyManager().isAvailable()) {
            player.sendMessage(color(plugin.getConfig().getString("messages.prefix", "")) + color(plugin.msg("cashout-no-economy")));
            return;
        }

        double rate = plugin.getConfig().getDouble("economy.cookies-per-money", 100.0);
        long cookies = data.getCookies();
        double money = rate > 0 ? cookies / rate : 0;

        data.setCookies(0);
        plugin.getEconomyManager().deposit(player, money);
        playSound(player, "cashout");

        String message = plugin.msg("cashout-success")
                .replace("%cookies%", String.valueOf(cookies))
                .replace("%money%", plugin.getEconomyManager().format(money));
        player.sendMessage(color(plugin.getConfig().getString("messages.prefix", "")) + color(message));

        gui.refresh(inv, player);
    }

    private void playSound(Player player, String key) {
        String soundName = plugin.getConfig().getString("sounds." + key);
        if (soundName == null) return;
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1f, 1f);
        } catch (IllegalArgumentException ignored) {
            // ungueltiger Sound-Name in der config.yml, einfach ignorieren
        }
    }

    private String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}

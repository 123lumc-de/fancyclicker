package com.lmcstudios.fancyclicker.listeners;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import com.lmcstudios.fancyclicker.data.PlayerData;
import com.lmcstudios.fancyclicker.gui.ClickerGUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GUIListener implements Listener {

    private final FancyClickerPlugin plugin;
    private final ClickerGUI gui;

    public GUIListener(FancyClickerPlugin plugin) {
        this.plugin = plugin;
        this.gui = new ClickerGUI(plugin);
    }

    public void open(Player player) {
        Inventory inv = gui.build(player);
        player.openInventory(inv);
    }

    public void reloadGui() {
        gui.loadGuiConfig();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ClickerGUI.CookieHolder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) return;

        PlayerData data = plugin.getDataManager().get(player);
        ClickType type = event.getClick();

        switch (slot) {
            case ClickerGUI.SLOT_INFO -> {
                if (type == ClickType.RIGHT || type == ClickType.SHIFT_RIGHT) {
                    handleCashOut(player, data, event.getInventory());
                }
            }
            case ClickerGUI.SLOT_SETTINGS -> handleToggle(player, data, event.getInventory());
            case ClickerGUI.SLOT_LEVELUP -> handleLevelUp(player, data, event.getInventory());
            default -> { }
        }
    }

    private void handleToggle(Player player, PlayerData data, Inventory inv) {
        data.togglePreference();
        plugin.getDataManager().saveAll();
        playSound(player, "click");
        gui.refresh(inv, player);
    }

    private void handleLevelUp(Player player, PlayerData data, Inventory inv) {
        long cost = gui.nextLevelCost(data.getLevel());
        if (!data.removeCookies(cost)) {
            player.sendMessage(prefix() + color("&7Du hast nicht genug Cookies fuer das naechste Level."));
            return;
        }
        data.levelUp();
        plugin.getDataManager().saveAll();
        playSound(player, "levelup");
        gui.refresh(inv, player);
    }

    private void handleCashOut(Player player, PlayerData data, Inventory inv) {
        if (data.getCookies() <= 0) {
            player.sendMessage(prefix() + color(plugin.msg("cashout-none")));
            return;
        }
        if (!plugin.getConfig().getBoolean("economy.enabled", true)
                || !plugin.getEconomyManager().isAvailable()) {
            player.sendMessage(prefix() + color(plugin.msg("cashout-no-economy")));
            return;
        }

        double rate = plugin.getConfig().getDouble("economy.cookies-per-money", 100.0);
        long cookies = data.getCookies();
        double money = rate > 0 ? cookies / rate : 0;

        data.setCookies(0);
        plugin.getEconomyManager().deposit(player, money);
        plugin.getDataManager().saveAll();
        playSound(player, "cashout");

        String message = plugin.msg("cashout-success")
                .replace("%cookies%", String.valueOf(cookies))
                .replace("%money%", plugin.getEconomyManager().format(money));
        player.sendMessage(prefix() + color(message));

        gui.refresh(inv, player);
    }

    private void playSound(Player player, String key) {
        String soundName = plugin.getConfig().getString("sounds." + key);
        if (soundName == null) return;
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1f, 1f);
        } catch (IllegalArgumentException ignored) { }
    }

    private String prefix() {
        return color(plugin.getConfig().getString("messages.prefix", ""));
    }

    private String color(String s) {
        if (s == null) return "";
        s = translateHex(s);
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String translateHex(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '#' && i + 7 <= input.length()) {
                String hex = input.substring(i + 1, i + 7);
                if (hex.matches("[0-9a-fA-F]{6}")) {
                    sb.append(net.md_5.bungee.api.ChatColor.of("#" + hex));
                    i += 7;
                    continue;
                }
            }
            sb.append(input.charAt(i));
            i++;
        }
        return sb.toString();
    }
}
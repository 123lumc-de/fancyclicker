package com.gerhartstudios.cookieclicker.gui;

import com.gerhartstudios.cookieclicker.CookieClickerPlugin;
import com.gerhartstudios.cookieclicker.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class ClickerGUI {

    public static final int SLOT_INFO = 10;
    public static final int SLOT_COOKIE = 13;
    public static final int SLOT_LEVELUP = 16;
    public static final int SLOT_CASHOUT = 22;

    private final CookieClickerPlugin plugin;

    public ClickerGUI(CookieClickerPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        int size = plugin.getConfig().getInt("gui.size", 27);
        String title = color(plugin.getConfig().getString("gui.title", "&8Cookie Clicker"));
        Inventory inv = Bukkit.createInventory(new CookieHolder(), size, title);

        ItemStack filler = namedItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, filler);
        }

        refresh(inv, player);
        return inv;
    }

    public void refresh(Inventory inv, Player player) {
        PlayerData data = plugin.getDataManager().get(player);
        long nextCost = nextLevelCost(data.getLevel());
        long perClick = clickValue(data.getLevel());

        List<String> infoLore = new ArrayList<>();
        infoLore.add(color("&7Cookies: &f" + data.getCookies()));
        infoLore.add(color("&7Insgesamt gesammelt: &f" + data.getTotalCookies()));
        infoLore.add(color("&7Level: &f" + data.getLevel()));
        infoLore.add(color("&7Pro Klick: &f" + perClick));
        inv.setItem(SLOT_INFO, namedItem(Material.PAPER, color("&b&lDeine Statistik"), infoLore));

        List<String> cookieLore = new ArrayList<>();
        cookieLore.add(color("&7Klicke mich fuer Cookies!"));
        cookieLore.add(color("&7+" + perClick + " Cookies pro Klick"));
        inv.setItem(SLOT_COOKIE, namedItem(Material.COOKIE, color("&6&lCookie"), cookieLore));

        List<String> levelLore = new ArrayList<>();
        levelLore.add(color("&7Aktuelles Level: &f" + data.getLevel()));
        levelLore.add(color("&7Naechstes Level: &f" + (data.getLevel() + 1)));
        levelLore.add(color("&7Preis: &f" + nextCost + " Cookies"));
        levelLore.add(color(data.getCookies() >= nextCost ? "&a&lKlicken zum Kaufen" : "&c&lNicht genug Cookies"));
        inv.setItem(SLOT_LEVELUP, namedItem(Material.EXPERIENCE_BOTTLE, color("&d&lLevel Upgrade"), levelLore));

        List<String> cashLore = new ArrayList<>();
        cashLore.add(color("&7Cookies: &f" + data.getCookies()));
        double rate = plugin.getConfig().getDouble("economy.cookies-per-money", 100.0);
        double moneyValue = rate > 0 ? data.getCookies() / rate : 0;
        cashLore.add(color("&7Wert: &f" + String.format("%.2f", moneyValue)));
        cashLore.add(color("&e&lKlicken zum Einloesen"));
        inv.setItem(SLOT_CASHOUT, namedItem(Material.GOLD_INGOT, color("&e&lCash Out"), cashLore));
    }

    public static long clickValue(int level) {
        return level;
    }

    public long nextLevelCost(int currentLevel) {
        long baseCost = plugin.getConfig().getLong("level-up.base-cost", 5000);
        return baseCost * (long) currentLevel;
    }

    private ItemStack namedItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /** Marker-Klasse, um GUI-Inventare eindeutig zu erkennen. */
    public static class CookieHolder implements org.bukkit.inventory.InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}

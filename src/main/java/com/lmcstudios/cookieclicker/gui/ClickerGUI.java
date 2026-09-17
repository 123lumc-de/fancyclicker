package com.lmcstudios.cookieclicker.gui;

import com.lmcstudios.cookieclicker.CookieClickerPlugin;
import com.lmcstudios.cookieclicker.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ClickerGUI {

    public static final int SLOT_INFO = 11;      // Paper - Linksklick Info, Rechtsklick Cash-Out
    public static final int SLOT_SETTINGS = 13;  // Comparator - Modus toggeln
    public static final int SLOT_LEVELUP = 15;   // Chest - Level Up

    private final CookieClickerPlugin plugin;

    public ClickerGUI(CookieClickerPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory build(Player player) {
        int size = plugin.getConfig().getInt("gui.size", 27);
        String title = color(plugin.getConfig().getString("gui.title", "&8Cookie Clicker"));

        CookieHolder holder = new CookieHolder();
        Inventory inv = Bukkit.createInventory(holder, size, title);
        holder.setInventory(inv);

        ItemStack filler = namedItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < size; i++) inv.setItem(i, filler);

        refresh(inv, player);
        return inv;
    }

    public void refresh(Inventory inv, Player player) {
        PlayerData data = plugin.getDataManager().get(player);
        long nextCost = nextLevelCost(data.getLevel());
        long perClick = clickValue(data.getLevel());

        // ---- Slot 11: Paper (Info + Cash Out) ----
        Material infoMat = Material.matchMaterial(
                plugin.getConfig().getString("gui-items.info.material", "PAPER"));
        String infoName = color(plugin.getConfig().getString("gui-items.info.name", "&e&lInfo &7& Cash Out"));

        List<String> infoLore = new ArrayList<>();
        infoLore.add(color("&7Cookies: &f" + data.getCookies()));
        infoLore.add(color("&7Insgesamt: &f" + data.getTotalCookies()));
        infoLore.add(color("&7Level: &f" + data.getLevel()));
        infoLore.add(color("&7Pro Klick: &f" + perClick));
        infoLore.add("");
        double rate = plugin.getConfig().getDouble("economy.cookies-per-money", 100.0);
        double moneyValue = rate > 0 ? data.getCookies() / rate : 0;
        infoLore.add(color("&6Cash Out:"));
        infoLore.add(color("&7  Wert: &f" + String.format("%.2f", moneyValue)));
        infoLore.add("");
        infoLore.add(color("&eLinksklick &7= Info"));
        infoLore.add(color("&eRechtsklick &7= Cash Out"));
        inv.setItem(SLOT_INFO, namedItem(infoMat == null ? Material.PAPER : infoMat, infoName, infoLore));

        // ---- Slot 13: Comparator (Settings) ----
        Material setMat = Material.matchMaterial(
                plugin.getConfig().getString("gui-items.settings.material", "COMPARATOR"));
        String setName = color(plugin.getConfig().getString("gui-items.settings.name", "&b&lSettings"));

        boolean leftFarms = data.getPreference() == PlayerData.Preference.LEFT;
        List<String> setLore = new ArrayList<>();
        setLore.add(color("&7Aktueller Modus:"));
        setLore.add(color(leftFarms
                ? "&a  Linksklick &7= Farmen"
                : "&a  Rechtsklick &7= Farmen"));
        setLore.add(color(leftFarms
                ? "&c  Rechtsklick &7= Menue"
                : "&c  Linksklick &7= Menue"));
        setLore.add("");
        setLore.add(color("&eKlicken zum Wechseln"));
        inv.setItem(SLOT_SETTINGS, namedItem(setMat == null ? Material.COMPARATOR : setMat, setName, setLore));

        // ---- Slot 15: Chest (Level Up) ----
        Material lvlMat = Material.matchMaterial(
                plugin.getConfig().getString("gui-items.levelup.material", "CHEST"));
        String lvlName = color(plugin.getConfig().getString("gui-items.levelup.name", "&d&lLevel Upgrade")
                .replace("%level%", String.valueOf(data.getLevel())));

        List<String> lvlLore = new ArrayList<>();
        lvlLore.add(color("&7Aktuelles Level: &f" + data.getLevel()));
        lvlLore.add(color("&7Naechstes Level: &f" + (data.getLevel() + 1)));
        lvlLore.add(color("&7Preis: &f" + nextCost + " Cookies"));
        lvlLore.add(color("&7Pro Klick danach: &f" + clickValue(data.getLevel() + 1)));
        lvlLore.add("");
        lvlLore.add(color(data.getCookies() >= nextCost
                ? "&a&lKlicken zum Kaufen"
                : "&c&lNicht genug Cookies"));
        inv.setItem(SLOT_LEVELUP, namedItem(lvlMat == null ? Material.CHEST : lvlMat, lvlName, lvlLore));
    }

    public static long clickValue(int level) { return level; }

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

    public static class CookieHolder implements org.bukkit.inventory.InventoryHolder {
        private Inventory inventory;

        @Override
        public Inventory getInventory() { return inventory; }

        public void setInventory(Inventory inventory) { this.inventory = inventory; }
    }
}

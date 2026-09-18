package com.lmcstudios.fancyclicker.gui;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import com.lmcstudios.fancyclicker.data.PlayerData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClickerGUI {

    public static final int SLOT_INFO = 11;
    public static final int SLOT_SETTINGS = 13;
    public static final int SLOT_LEVELUP = 15;

    private final FancyClickerPlugin plugin;
    private FileConfiguration guiConfig;

    public ClickerGUI(FancyClickerPlugin plugin) {
        this.plugin = plugin;
        loadGuiConfig();
    }

    public void loadGuiConfig() {
        File file = new File(plugin.getDataFolder(), "gui.yml");
        if (!file.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(file);
    }

    public Inventory build(Player player) {
        int size = plugin.getConfig().getInt("gui.size", 27);
        String title = color(plugin.getConfig().getString("gui.title", "&8Fancy Clicker"));

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
        long perClick = clickValue(data.getLevel());
        long nextCost = nextLevelCost(data.getLevel());

    double rate = plugin.getConfig().getDouble("economy.money-per-cookie", 0.1);
    double moneyValue = data.getCookies() * rate;

        inv.setItem(SLOT_INFO, buildItem(
                "items.info",
                "%cookies%", String.valueOf(data.getCookies()),
                "%perclick%", String.valueOf(perClick),
                "%money%", String.format("%.2f", moneyValue)
        ));

        boolean leftFarms = data.getPreference() == PlayerData.Preference.LEFT;
        String rcAction = leftFarms ? "Cookie Clicker" : "Menu";
        String lcAction = leftFarms ? "Menu" : "Cookie Clicker";

        inv.setItem(SLOT_SETTINGS, buildItem(
                "items.settings",
                "%rc_action%", rcAction,
                "%lc_action%", lcAction
        ));

        String status = data.getCookies() >= nextCost
                ? "#25FF95Klicken zum Kaufen"
                : "&8Nicht genug Cookies";

        inv.setItem(SLOT_LEVELUP, buildItem(
                "items.levelup",
                "%level%", String.valueOf(data.getLevel()),
                "%nextlevel%", String.valueOf(data.getLevel() + 1),
                "%price%", String.valueOf(nextCost),
                "%status%", status
        ));
    }

    private ItemStack buildItem(String path, String... replacements) {
        String matName = guiConfig.getString(path + ".material", "PAPER");
        Material material = Material.matchMaterial(matName);
        if (material == null) material = Material.PAPER;

        String name = color(guiConfig.getString(path + ".name", " "));
        List<String> loreRaw = guiConfig.getStringList(path + ".lore");
        List<String> lore = new ArrayList<>();

        for (String line : loreRaw) {
            for (int i = 0; i + 1 < replacements.length; i += 2) {
                line = line.replace(replacements[i], replacements[i + 1]);
            }
            lore.add(color(line));
        }
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            name = name.replace(replacements[i], replacements[i + 1]);
        }
        name = color(name);

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
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

    public static long clickValue(int level) { return level; }

    public long nextLevelCost(int currentLevel) {
        long baseCost = plugin.getConfig().getLong("level-up.base-cost", 5000);
        return baseCost * (long) currentLevel;
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

    public static class CookieHolder implements org.bukkit.inventory.InventoryHolder {
        private Inventory inventory;

        @Override
        public Inventory getInventory() { return inventory; }

        public void setInventory(Inventory inventory) { this.inventory = inventory; }
    }
}
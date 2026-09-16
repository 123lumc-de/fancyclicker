package com.gerhartstudios.cookieclicker;

import com.gerhartstudios.cookieclicker.commands.CCCommand;
import com.gerhartstudios.cookieclicker.data.DataManager;
import com.gerhartstudios.cookieclicker.economy.EconomyManager;
import com.gerhartstudios.cookieclicker.listeners.GUIListener;
import com.gerhartstudios.cookieclicker.listeners.InteractListener;
import com.gerhartstudios.cookieclicker.placeholder.CookieClickerExpansion;
import com.gerhartstudios.cookieclicker.util.BindManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CookieClickerPlugin extends JavaPlugin {

    private DataManager dataManager;
    private BindManager bindManager;
    private EconomyManager economyManager;
    private GUIListener guiListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getDataFolder().mkdirs();

        this.dataManager = new DataManager(this);
        this.bindManager = new BindManager(this);
        this.economyManager = new EconomyManager(this);
        this.guiListener = new GUIListener(this);

        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getServer().getPluginManager().registerEvents(guiListener, this);

        CCCommand ccCommand = new CCCommand(this);
        var command = getCommand("cc");
        if (command != null) {
            command.setExecutor(ccCommand);
            command.setTabCompleter(ccCommand);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CookieClickerExpansion(this).register();
            getLogger().info("PlaceholderAPI gefunden - Placeholder registriert (%cookieclicker_total%, %cookieclicker_currently%, %cookieclicker_level%).");
        }

        if (!economyManager.isAvailable()) {
            getLogger().warning("Vault/Economy nicht gefunden - Cash-Out ist deaktiviert bis eine Economy verfuegbar ist.");
        }

        getLogger().info("CookieClicker wurde aktiviert.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveAll();
        }
        getLogger().info("CookieClicker wurde deaktiviert.");
    }

    public String msg(String key) {
        return getConfig().getString("messages." + key, key);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public BindManager getBindManager() {
        return bindManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public GUIListener getGuiListener() {
        return guiListener;
    }
}

package com.lmcstudios.fancyclicker;

import com.lmcstudios.fancyclicker.commands.FCCommand;
import com.lmcstudios.fancyclicker.data.DataManager;
import com.lmcstudios.fancyclicker.economy.EconomyManager;
import com.lmcstudios.fancyclicker.listeners.GUIListener;
import com.lmcstudios.fancyclicker.listeners.InteractListener;
import com.lmcstudios.fancyclicker.placeholder.FancyClickerExpansion;
import com.lmcstudios.fancyclicker.util.BindManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyClickerPlugin extends JavaPlugin {

    // bStats-Plugin-ID
    private static final int BSTATS_PLUGIN_ID = 34103;

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

        FCCommand fcCommand = new FCCommand(this);
        var command = getCommand("fc");
        if (command != null) {
            command.setExecutor(fcCommand);
            command.setTabCompleter(fcCommand);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FancyClickerExpansion(this).register();
            getLogger().info("PlaceholderAPI gefunden - Placeholder registriert.");
        }

        if (!getConfig().getBoolean("economy.enabled", true)) {
            getLogger().info("Cash-Out ist in der config.yml deaktiviert.");
        } else if (!economyManager.isAvailable()) {
            getLogger().warning("Vault/Economy nicht gefunden - Cash-Out ist deaktiviert.");
        }

        setupBStats();

        getLogger().info("FancyClicker wurde aktiviert.");
    }

    private void setupBStats() {
        Metrics metrics = new Metrics(this, BSTATS_PLUGIN_ID);

        // Zeigt, ob Server Economy aktiviert haben
        metrics.addCustomChart(new SimplePie("economy_enabled", () ->
                String.valueOf(getConfig().getBoolean("economy.enabled", true))));

        // Zeigt die genutzte Minecraft-Version
        metrics.addCustomChart(new SimplePie("minecraft_version", () ->
                Bukkit.getMinecraftVersion()));

        // Zeigt die Server-Software (Paper, Purpur, etc.)
        metrics.addCustomChart(new SimplePie("server_software", () ->
                Bukkit.getName()));
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.saveAll();
        if (bindManager != null) bindManager.save();
        getLogger().info("FancyClicker wurde deaktiviert.");
    }

    public String msg(String key) {
        return getConfig().getString("messages." + key, key);
    }

    public DataManager getDataManager() { return dataManager; }
    public BindManager getBindManager() { return bindManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public GUIListener getGuiListener() { return guiListener; }
}

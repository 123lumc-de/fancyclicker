package com.lmcstudios.cookieclicker.util;

import com.lmcstudios.cookieclicker.CookieClickerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class BindManager {

    private final CookieClickerPlugin plugin;
    private final File file;
    private Location boundLocation;

    public BindManager(CookieClickerPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "bind.yml");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (!cfg.contains("world")) return;
        World world = Bukkit.getWorld(cfg.getString("world"));
        if (world == null) {
            plugin.getLogger().warning("Gebundene Welt nicht gefunden. Bindung ignoriert.");

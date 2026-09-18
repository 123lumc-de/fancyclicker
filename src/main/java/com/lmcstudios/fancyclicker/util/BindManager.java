package com.lmcstudios.fancyclicker.util;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BindManager {

    private final FancyClickerPlugin plugin;
    private final File file;
    private final Set<String> boundBlocks = new HashSet<>();

    public BindManager(FancyClickerPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "bind.yml");
        load();
    }

    private String key(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }

    private void load() {
        if (!file.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = cfg.getConfigurationSection("blocks");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            boundBlocks.add(key);
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (String key : boundBlocks) {
            cfg.set("blocks." + key, true);
        }
        try {
            plugin.getDataFolder().mkdirs();
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte bind.yml nicht speichern: " + e.getMessage());
        }
    }

    public boolean isBound() {
        return !boundBlocks.isEmpty();
    }

    public boolean isBoundBlock(Location location) {
        if (location == null || location.getWorld() == null) return false;
        return boundBlocks.contains(key(location));
    }

    public void bind(Location location) {
        boundBlocks.add(key(location));
        save();
    }

    public void unbind(Location location) {
        boundBlocks.remove(key(location));
        save();
    }

    /** Entfernt alle Bindungen. */
    public void unbindAll() {
        boundBlocks.clear();
        save();
    }

    public int getBoundCount() {
        return boundBlocks.size();
    }

    public Set<String> getAllKeys() {
        return new HashSet<>(boundBlocks);
    }
}
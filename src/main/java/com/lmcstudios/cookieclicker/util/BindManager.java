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
            return;
        }
        int x = cfg.getInt("x");
        int y = cfg.getInt("y");
        int z = cfg.getInt("z");
        boundLocation = new Location(world, x, y, z);
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        if (boundLocation != null) {
            cfg.set("world", boundLocation.getWorld().getName());
            cfg.set("x", boundLocation.getBlockX());
            cfg.set("y", boundLocation.getBlockY());
            cfg.set("z", boundLocation.getBlockZ());
        }
        try {
            plugin.getDataFolder().mkdirs();
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte bind.yml nicht speichern: " + e.getMessage());
        }
    }

    public boolean isBound() {
        return boundLocation != null;
    }

    public boolean isBoundBlock(Location location) {
        if (boundLocation == null || location == null) return false;
        if (!boundLocation.getWorld().equals(location.getWorld())) return false;
        return boundLocation.getBlockX() == location.getBlockX()
                && boundLocation.getBlockY() == location.getBlockY()
                && boundLocation.getBlockZ() == location.getBlockZ();
    }

    public void bind(Location location) {
        this.boundLocation = location.clone();
        save();
    }

    public void unbind() {
        this.boundLocation = null;
        save();
    }

    public Location getBoundLocation() {
        return boundLocation;
    }
}

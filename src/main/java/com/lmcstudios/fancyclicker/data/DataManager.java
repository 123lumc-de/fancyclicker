package com.lmcstudios.fancyclicker.data;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final FancyClickerPlugin plugin;
    private final File file;
    private YamlConfiguration config;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public DataManager(FancyClickerPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playerdata.yml");
        load();
    }

    private void load() {
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Konnte playerdata.yml nicht erstellen: " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveAll() {
        for (PlayerData data : cache.values()) writeToConfig(data);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Konnte playerdata.yml nicht speichern: " + e.getMessage());
        }
    }

    private void writeToConfig(PlayerData data) {
        String path = data.getUuid().toString();
        config.set(path + ".cookies", data.getCookies());
        config.set(path + ".total", data.getTotalCookies());
        config.set(path + ".level", data.getLevel());
        config.set(path + ".preference", data.getPreference().name());
    }

    public PlayerData get(UUID uuid) {
        if (cache.containsKey(uuid)) return cache.get(uuid);
        String path = uuid.toString();
        long cookies = config.getLong(path + ".cookies", 0);
        long total = config.getLong(path + ".total", 0);
        int level = config.getInt(path + ".level", 1);
        PlayerData.Preference pref;
        try {
            pref = PlayerData.Preference.valueOf(config.getString(path + ".preference", "LEFT"));
        } catch (IllegalArgumentException e) {
            pref = PlayerData.Preference.LEFT;
        }
        PlayerData data = new PlayerData(uuid, cookies, total, level, pref);
        cache.put(uuid, data);
        return data;
    }

    public PlayerData get(OfflinePlayer player) { return get(player.getUniqueId()); }

    public void reset(UUID uuid) {
        cache.remove(uuid);
        config.set(uuid.toString(), null);
        PlayerData fresh = new PlayerData(uuid, 0, 0, 1, PlayerData.Preference.LEFT);
        cache.put(uuid, fresh);
        saveAll();
    }

    public boolean hasData(UUID uuid) {
        return cache.containsKey(uuid) || config.contains(uuid.toString());
    }
}
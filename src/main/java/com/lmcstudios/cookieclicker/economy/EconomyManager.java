package com.lmcstudios.cookieclicker.economy;

import com.lmcstudios.cookieclicker.CookieClickerPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private final CookieClickerPlugin plugin;
    private Economy economy;

    public EconomyManager(CookieClickerPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public boolean isAvailable() {
        if (economy == null) {
            setup(); // Late-Binding: Vault kann spaeter geladen werden
        }
        return economy != null;
    }

    public double deposit(OfflinePlayer player, double amount) {
        if (economy == null) return 0;
        economy.depositPlayer(player, amount);
        return amount;
    }

    public String format(double amount) {
        if (economy == null) return String.valueOf(amount);
        return economy.format(amount);
    }
}

package com.lmcstudios.fancyclicker.economy;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private final FancyClickerPlugin plugin;
    private Economy economy;

    public EconomyManager(FancyClickerPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return true;
    }

    public boolean isAvailable() {
        if (economy == null) setup();
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
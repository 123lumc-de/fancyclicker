package com.lmcstudios.cookieclicker.listeners;

import com.lmcstudios.cookieclicker.CookieClickerPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private final CookieClickerPlugin plugin;

    public InteractListener(CookieClickerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!plugin.getBindManager().isBoundBlock(event.getClickedBlock().getLocation())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (!player.hasPermission("cookieclicker.use")) {
            player.sendMessage(prefix() + color(plugin.msg("no-permission")));
            return;
        }

        plugin.getGuiListener().open(player);
    }

    private String prefix() {
        return color(plugin.getConfig().getString("messages.prefix", ""));
    }

    private String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}

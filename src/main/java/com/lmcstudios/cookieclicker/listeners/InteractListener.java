package com.lmcstudios.cookieclicker.listeners;

import com.lmcstudios.cookieclicker.CookieClickerPlugin;
import com.lmcstudios.cookieclicker.data.PlayerData;
import com.lmcstudios.cookieclicker.gui.ClickerGUI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (!plugin.getBindManager().isBoundBlock(event.getClickedBlock().getLocation())) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (!player.hasPermission("cookieclicker.use")) {
            player.sendMessage(prefix() + color(plugin.msg("no-permission")));
            return;
        }

        PlayerData data = plugin.getDataManager().get(player);
        boolean leftFarms = data.getPreference() == PlayerData.Preference.LEFT;

        boolean isLeft = action == Action.LEFT_CLICK_BLOCK;
        boolean shouldFarm = (isLeft && leftFarms) || (!isLeft && !leftFarms);

        if (shouldFarm) {
            handleFarm(player, data, event.getClickedBlock());
        } else {
            plugin.getGuiListener().open(player);
        }
    }

    private void handleFarm(Player player, PlayerData data, Block block) {
        long value = ClickerGUI.clickValue(data.getLevel());
        data.addCookies(value);
        plugin.getDataManager().saveAll();

        // Sound
        String soundName = plugin.getConfig().getString("sounds.click", "BLOCK_STONE_BREAK");
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1f, 1f);
        } catch (IllegalArgumentException ignored) { }

        // ActionBar
        player.sendActionBar(net.kyori.adventure.text.Component.text(
                ChatColor.GOLD + "+" + value + " Cookies"));

        // Fake-Damage-Animation
        player.sendBlockDamage(block.getLocation(), 1.0f);
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> player.sendBlockDamage(block.getLocation(), 0f), 5L);
    }

    private String prefix() {
        return color(plugin.getConfig().getString("messages.prefix", ""));
    }

    private String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}

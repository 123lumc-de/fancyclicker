package com.lmcstudios.fancyclicker.listeners;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import com.lmcstudios.fancyclicker.data.PlayerData;
import com.lmcstudios.fancyclicker.gui.ClickerGUI;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private final FancyClickerPlugin plugin;

    public InteractListener(FancyClickerPlugin plugin) {
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

        if (!player.hasPermission("fancyclicker.use")) {
            player.sendMessage(prefix() + color(plugin.msg("no-permission")));
            return;
        }

        PlayerData data = plugin.getDataManager().get(player);
        boolean leftFarms = data.getPreference() == PlayerData.Preference.LEFT;
        boolean isLeft = action == Action.LEFT_CLICK_BLOCK;
        boolean shouldFarm = (isLeft && leftFarms) || (!isLeft && !leftFarms);

        if (shouldFarm) {
            handleFarm(player, data);
        } else {
            plugin.getGuiListener().open(player);
        }
    }

    private void handleFarm(Player player, PlayerData data) {
        long value = ClickerGUI.clickValue(data.getLevel());
        data.addCookies(value);

        String soundName = plugin.getConfig().getString("sounds.click", "BLOCK_STONE_HIT");
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1f, 1f);
        } catch (IllegalArgumentException ignored) { }

    String bar = "<#25FF95>+" + value + " Cookies <gray>| <#25FF95>"
        + data.getCookies() + " Cookies";
    player.sendActionBar(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(bar));

    private String prefix() {
        return color(plugin.getConfig().getString("messages.prefix", ""));
    }

    private String color(String s) {
        if (s == null) return "";
        s = translateHex(s);
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private String translateHex(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '#' && i + 7 <= input.length()) {
                String hex = input.substring(i + 1, i + 7);
                if (hex.matches("[0-9a-fA-F]{6}")) {
                    sb.append(net.md_5.bungee.api.ChatColor.of("#" + hex));
                    i += 7;
                    continue;
                }
            }
            sb.append(input.charAt(i));
            i++;
        }
        return sb.toString();
    }
}
package com.lmcstudios.fancyclicker.commands;

import com.lmcstudios.fancyclicker.FancyClickerPlugin;
import com.lmcstudios.fancyclicker.data.PlayerData;
import com.lmcstudios.fancyclicker.util.NumberUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FCCommand implements CommandExecutor, TabCompleter {

    private final FancyClickerPlugin plugin;

    public FCCommand(FancyClickerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(prefix() + color(plugin.msg("usage")));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "bind" -> handleBind(sender);
            case "unbind" -> handleUnbind(sender);
            case "reload" -> handleReload(sender);
            case "reset" -> handleReset(sender, args);
            case "setcookie" -> handleSetCookie(sender, args);
            case "addcookie" -> handleAddCookie(sender, args);
            case "setlevel" -> handleSetLevel(sender, args);
            default -> sender.sendMessage(prefix() + color(plugin.msg("usage")));
        }
        return true;
    }

    private void handleBind(CommandSender sender) {
        if (!requireAdmin(sender)) return;
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return;
        }
        Block target = player.getTargetBlockExact(6);
        if (target == null || target.getType().isAir()) {
            player.sendMessage(prefix() + color(plugin.msg("bind-look-at-block")));
            return;
        }
        if (plugin.getBindManager().isBoundBlock(target.getLocation())) {
            player.sendMessage(prefix() + color("&7Dieser Block ist bereits gebunden."));
            return;
        }
        plugin.getBindManager().bind(target.getLocation());
        player.sendMessage(prefix() + color(plugin.msg("bind-success")
                .replace("%count%", String.valueOf(plugin.getBindManager().getBoundCount()))));
    }

    private void handleUnbind(CommandSender sender) {
        if (!requireAdmin(sender)) return;
        if (!plugin.getBindManager().isBound()) {
            sender.sendMessage(prefix() + color(plugin.msg("unbind-none")));
            return;
        }

        if (sender instanceof Player player) {
            Block target = player.getTargetBlockExact(6);
            if (target != null && !target.getType().isAir()
                    && plugin.getBindManager().isBoundBlock(target.getLocation())) {
                plugin.getBindManager().unbind(target.getLocation());
                player.sendMessage(prefix() + color(plugin.msg("unbind-success")
                        .replace("%count%", String.valueOf(plugin.getBindManager().getBoundCount()))));
                return;
            }
        }

        int count = plugin.getBindManager().getBoundCount();
        plugin.getBindManager().unbindAll();
        sender.sendMessage(prefix() + color(plugin.msg("unbind-all-success")
                .replace("%count%", String.valueOf(count))));
    }

    private void handleReload(CommandSender sender) {
        if (!requireAdmin(sender)) return;
        plugin.reloadConfig();
        plugin.getGuiListener().reloadGui();
        sender.sendMessage(prefix() + color(plugin.msg("reload-success")));
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (!requireAdmin(sender)) return;
        if (args.length < 2) {
            sender.sendMessage(prefix() + color(plugin.msg("usage")));
            return;
        }
        OfflinePlayer target = resolvePlayer(args[1]);
        if (target == null) {
            sender.sendMessage(prefix() + color(plugin.msg("player-not-found")));
            return;
        }
        plugin.getDataManager().reset(target.getUniqueId());
        sender.sendMessage(prefix() + color(plugin.msg("reset-success")
                .replace("%player%", displayName(target))));
    }

    private void handleSetCookie(CommandSender sender, String[] args) {
        if (!requireAdmin(sender)) return;
        if (args.length < 3) {
            sender.sendMessage(prefix() + color(plugin.msg("usage")));
            return;
        }
        OfflinePlayer target = resolvePlayer(args[1]);
        if (target == null) {
            sender.sendMessage(prefix() + color(plugin.msg("player-not-found")));
            return;
        }
        Long amount = parseLong(args[2]);
        if (amount == null || amount < 0) {
            sender.sendMessage(prefix() + color(plugin.msg("invalid-number")));
            return;
        }
        PlayerData data = plugin.getDataManager().get(target);
        data.setCookies(amount);
        plugin.getDataManager().saveAll();
        sender.sendMessage(prefix() + color(plugin.msg("setcookie-success")
                .replace("%player%", displayName(target))
                .replace("%amount%", NumberUtil.format(amount))));
    }

    private void handleAddCookie(CommandSender sender, String[] args) {
        if (!requireAdmin(sender)) return;
        if (args.length < 3) {
            sender.sendMessage(prefix() + color(plugin.msg("usage")));
            return;
        }
        OfflinePlayer target = resolvePlayer(args[1]);
        if (target == null) {
            sender.sendMessage(prefix() + color(plugin.msg("player-not-found")));
            return;
        }
        Long amount = parseLong(args[2]);
        if (amount == null || amount <= 0) {
            sender.sendMessage(prefix() + color(plugin.msg("invalid-number")));
            return;
        }
        PlayerData data = plugin.getDataManager().get(target);
        data.addCookies(amount);
        plugin.getDataManager().saveAll();
        sender.sendMessage(prefix() + color(plugin.msg("addcookie-success")
                .replace("%player%", displayName(target))
                .replace("%amount%", NumberUtil.format(amount))));
    }

    private void handleSetLevel(CommandSender sender, String[] args) {
        if (!requireAdmin(sender)) return;
        if (args.length < 3) {
            sender.sendMessage(prefix() + color(plugin.msg("usage")));
            return;
        }
        OfflinePlayer target = resolvePlayer(args[1]);
        if (target == null) {
            sender.sendMessage(prefix() + color(plugin.msg("player-not-found")));
            return;
        }
        Integer level = parseInt(args[2]);
        if (level == null || level < 1) {
            sender.sendMessage(prefix() + color(plugin.msg("invalid-number")));
            return;
        }
        PlayerData data = plugin.getDataManager().get(target);
        data.setLevel(level);
        plugin.getDataManager().saveAll();
        sender.sendMessage(prefix() + color(plugin.msg("setlevel-success")
                .replace("%player%", displayName(target))
                .replace("%level%", String.valueOf(level))));
    }

    private boolean requireAdmin(CommandSender sender) {
        if (!sender.hasPermission("fancyclicker.admin")) {
            sender.sendMessage(prefix() + color(plugin.msg("no-permission")));
            return false;
        }
        return true;
    }

    private OfflinePlayer resolvePlayer(String name) {
        OfflinePlayer target = Bukkit.getPlayerExact(name);
        if (target != null) return target;
        @SuppressWarnings("deprecation")
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        if (offline != null && (offline.hasPlayedBefore() || offline.isOnline())) {
            return offline;
        }
        return null;
    }

    private String displayName(OfflinePlayer player) {
        return player.getName() != null ? player.getName() : player.getUniqueId().toString();
    }

    private Long parseLong(String s) {
        try { return Long.parseLong(s); } catch (NumberFormatException e) { return null; }
    }

    private Integer parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> subs = List.of("bind", "unbind", "reload", "reset", "setcookie", "addcookie", "setlevel");
        if (args.length == 1) {
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && List.of("reset", "setcookie", "addcookie", "setlevel").contains(args[0].toLowerCase())) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

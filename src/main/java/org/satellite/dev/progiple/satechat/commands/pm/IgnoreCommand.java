package org.satellite.dev.progiple.satechat.commands.pm;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.commands.annotations.LunaCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.data.DataManager;

import java.util.ArrayList;
import java.util.List;

@LunaCommand("ignore")
public class IgnoreCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length <= 1 || !(commandSender instanceof Player player)) {
            Config.sendMessage(commandSender, "usage.satechat");
            return true;
        }

        if (strings[1].equalsIgnoreCase("all")) {
            if (!Tools.hasPermission(player, "satechat.ignore.all")) {
                Config.sendMessage(player, "noPermission");
                return true;
            }


            boolean b = DataManager.getConfig(player.getUniqueId()).switchIgnore();
            Config.sendMessage(player, "ignoreAll." + (b ? "enabling" : "disabling"));
            return true;
        }

        Player target = Bukkit.getPlayer(strings[1]);
        if (target == null || !target.isOnline()) {
            Config.sendMessage(player, "playerIsOffline", "player-%-" + strings[1]);
            return true;
        }

        if (!Tools.hasPermission(player, "satechat.ignore")) {
            Config.sendMessage(player, "noPermission");
            return true;
        }

        boolean b = DataManager.getConfig(player.getUniqueId()).switchIgnore(target);
        Config.sendMessage(player, "ignore." + (b ? "enabling" : "disabling"), "player-%-" + target.getName());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length != 1) return List.of();

        List<String> tab = new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        tab.add("all");
        return Utils.tabCompleterFiltering(tab, strings[0]);
    }
}

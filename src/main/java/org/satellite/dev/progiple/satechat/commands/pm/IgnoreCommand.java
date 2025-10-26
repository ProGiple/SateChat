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
import org.satellite.dev.progiple.satechat.users.ChatUserManager;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.List;

@LunaCommand("ignore")
public class IgnoreCommand implements TabExecutor {
    // ignore Siozik

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0 || !(sender instanceof Player player)) {
            Config.sendMessage(sender, "usage.ignore");
            return true;
        }

        IChatUser chatUser = ChatUserManager.get(player.getUniqueId());
        if (strings[0].equalsIgnoreCase("all")) {
            if (!Tools.hasPermission(player, "satechat.ignore.all")) {
                Config.sendMessage(player, "noPermission");
                return true;
            }

            boolean b = chatUser.switchIgnoreAll();
            Config.sendMessage(player, "ignoreAll." + (b ? "enabling" : "disabling"));
            return true;
        }

        Player target = Bukkit.getPlayer(strings[0]);
        if (target == null) {
            Config.sendMessage(player, "playerIsOffline", "player-%-" + strings[0]);
            return true;
        }

        if (!Tools.hasPermission(player, "satechat.ignore")) {
            Config.sendMessage(player, "noPermission");
            return true;
        }

        boolean b = chatUser.switchIgnore(target);
        Config.sendMessage(player, "ignore." + (b ? "enabling" : "disabling"), "player-%-" + target.getName());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length != 1) return null;

        List<String> tab = Utils.getPlayerNicks(strings[0]);
        tab.add("all");
        return tab;
    }
}

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
import org.satellite.dev.progiple.satechat.chats.state.PrivateManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.users.ChatUserManager;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.List;

@LunaCommand(value = "privatemessage")
public class PMCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!Tools.hasPermission(commandSender, "satechat.privatemessage")) {
            Config.sendMessage(commandSender, "noPermission");
            return true;
        }

        if (strings.length <= 1 || !(commandSender instanceof Player player)) {
            Config.sendMessage(commandSender, "usage.privatemessage");
            return true;
        }

        Player target = Bukkit.getPlayer(strings[0]);
        if (target == null || !target.isOnline()) {
            Config.sendMessage(commandSender, "playerIsOffline", strings[0]);
            return true;
        }

        IChatUser senderUser = ChatUserManager.get(player.getUniqueId());
        IChatUser recipientUser = ChatUserManager.get(target.getUniqueId());
        PrivateManager.sendPrivate(senderUser, recipientUser, List.of(strings).subList(1, strings.length));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? Utils.getPlayerNicks(strings[0]) : (strings.length == 2 ? List.of("<message>") : List.of());
    }
}

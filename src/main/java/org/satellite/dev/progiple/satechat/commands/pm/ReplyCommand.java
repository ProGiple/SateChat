package org.satellite.dev.progiple.satechat.commands.pm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.commands.annotations.LunaCommand;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.chats.state.PrivateManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.users.ChatUserManager;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.List;

@LunaCommand("reply")
public class ReplyCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!Tools.hasPermission(commandSender, "satechat.reply")) {
            Config.sendMessage(commandSender, "noPermission");
            return true;
        }

        if (strings.length == 0 || !(commandSender instanceof Player player)) {
            Config.sendMessage(commandSender, "usage.reply");
            return true;
        }

        IChatUser chatUser = ChatUserManager.get(player.getUniqueId());
        if (!PrivateManager.reply(chatUser, List.of(strings).subList(0, strings.length))) Config.sendMessage(player, "noReply");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? List.of("<message>") : null;
    }
}

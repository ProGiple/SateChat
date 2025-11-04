package org.satellite.dev.progiple.satechat.commands.another;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.commands.annotations.LunaCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.utils.Tools;
import org.satellite.dev.progiple.satechat.chats.state.ChatManager;
import org.satellite.dev.progiple.satechat.configs.ads.AdsManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.replacements.ReplacementsManager;
import org.satellite.dev.progiple.satechat.configs.swears.SwearsManager;
import org.satellite.dev.progiple.satechat.users.ChatUserManager;

import java.util.List;

@LunaCommand("satechat")
public class SateChatCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            Config.sendMessage(sender, "usage.satechat");
            return true;
        }

        if (strings[0].equalsIgnoreCase("spy")) {
            if (!(sender instanceof Player player)) {
                Config.sendMessage(sender, "usage.satechat");
                return true;
            }

            if (!Tools.hasPermission(player, "satechat.spy")) {
                Config.sendMessage(player, "noPermission");
                return true;
            }

            boolean b = ChatUserManager.get(player.getUniqueId()).switchSpy();
            Config.sendMessage(player, "spy." + (b ? "enabling" : "disabling"));
        }
        else {
            if (!Tools.hasPermission(sender, "satechat.reload")) {
                Config.sendMessage(sender, "noPermission");
                return true;
            }

            Config.reload();
            ChatManager.reload();
            ReplacementsManager.reload();
            SwearsManager.reload();
            AdsManager.reload();
            Config.sendMessage(sender, "reload");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? Utils.tabCompleterFiltering(List.of("spy", "reload"), strings[0]) : null;
    }
}

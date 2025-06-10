package org.satellite.dev.progiple.satechat.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.data.DataManager;

@SubCommand(appliedCommand = "satechat", commandIdentifiers = {"spy"})
@Check(permissions = {}, flags = {})
public class SpySubCommand implements Invocation {
    @Override
    public void invoke(CommandSender commandSender, String[] strings) {
        if (strings.length == 0 || !(commandSender instanceof Player player)) {
            Config.sendMessage(commandSender, "usage.satechat");
            return;
        }

        if (!Tools.hasPermission(player, "satechat.spy")) {
            Config.sendMessage(player, "noPermission");
            return;
        }

        boolean b = DataManager.getConfig(player.getUniqueId()).switchSpyMode();
        Config.sendMessage(player, "spy." + (b ? "enabling" : "disabling"));
    }
}

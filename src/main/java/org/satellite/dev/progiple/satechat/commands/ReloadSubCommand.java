package org.satellite.dev.progiple.satechat.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.chats.ChatManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.data.DataConfig;
import org.satellite.dev.progiple.satechat.configs.data.DataManager;

@SubCommand(appliedCommand = "satechat", commandIdentifiers = {"reload"})
@Check(permissions = {}, flags = {})
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender commandSender, String[] strings) {
        if (!Tools.hasPermission(commandSender, "satechat.reload")) {
            Config.sendMessage(commandSender, "noPermission");
            return;
        }

        Config.reload();
        ChatManager.reload();
        DataManager.getConfigs().forEach(DataConfig::reload);
        Config.sendMessage(commandSender, "reload");
    }
}

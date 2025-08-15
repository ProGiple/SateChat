package org.satellite.dev.progiple.satechat.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.chats.state.ChatManager;
import org.satellite.dev.progiple.satechat.configs.AdsConfig;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.ReplacementsConfig;
import org.satellite.dev.progiple.satechat.configs.SwearsConfig;

@SubCommand(appliedCommand = "satechat", commandIdentifiers = {"reload"})
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender commandSender, String[] strings) {
        if (!Tools.hasPermission(commandSender, "satechat.reload")) {
            Config.sendMessage(commandSender, "noPermission");
            return;
        }

        Config.reload();
        ChatManager.reload();
        ReplacementsConfig.get().reload();
        SwearsConfig.get().reload();
        AdsConfig.get().reload();
        Config.sendMessage(commandSender, "reload");
    }
}

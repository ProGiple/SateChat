package org.satellite.dev.progiple.satechat.configs.ads;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.utils.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.IFileConfig;

import java.util.List;

public interface IAdsConfig extends IFileConfig {
    List<String> getFormats();
    List<String> getWhitelist();
    Mode getMode();
    void reload();
    default String process(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "ads") || !isEnabled()) return message;

        List<String> patterns = getFormats();
        String whitelistedMessage = message;

        List<String> whitelist = getWhitelist();
        for (String s : whitelist) {
            whitelistedMessage = whitelistedMessage.replace(s, "");
        }

        String replacerValue = whitelist.isEmpty() ? "" : whitelist.get(0);
        String checkedMessage = whitelistedMessage;
        for (String pattern : patterns) {
            checkedMessage = checkedMessage.replaceAll(pattern, replacerValue);
        }

        if (whitelistedMessage.equalsIgnoreCase(checkedMessage)) return message;
        if (!Tools.CacheValue.ADS.has(sender)) {
            Bukkit.getScheduler().runTaskLater(SateChat.getINSTANCE(), () -> {
                Config.sendMessage(sender, "ads_warn");
                Tools.dispatch(getActionCommands(), sender.getName());
            }, 2L);
        }

        Tools.CacheValue.ADS.push(sender);
        return getMode() == Mode.BLOCK ? null : checkedMessage;
    }

    enum Mode {
        BLOCK,
        REPLACE
    }
}

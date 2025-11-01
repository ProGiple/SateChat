package org.satellite.dev.progiple.satechat.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.Tools;

import javax.annotation.Nullable;
import java.util.List;

@UtilityClass
public class AdsConfig {
    private IConfig config;

    public void initialize(SateChat sateChat) {
        config = new IConfig(sateChat.getDataFolder(), Config.getString("advertisement_block.file"));
    }

    public List<String> getFormats() {
        return config.getStringList("formats");
    }

    public List<String> getWhitelist() {
        return config.getStringList("whitelist");
    }

    public Mode getMode() {
        return Utils.getEnumValue(Mode.class, config.getString("mode"), Mode.BLOCK);
    }

    public void reload() {
        config.reload();
    }

    public @Nullable String block(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "ads")) return message;

        ConfigurationSection section = Config.getSection("advertisement_block");
        if (!section.getBoolean("enable")) return message;

        List<String> patterns = AdsConfig.getFormats();

        String checkedMessage = message;

        List<String> whitelist = AdsConfig.getWhitelist();
        String replacerValue = "";
        for (String s : whitelist) {
            if (replacerValue.isEmpty()) replacerValue = s;
            checkedMessage = checkedMessage.replace(s, "");
        }

        for (String pattern : patterns) {
            message = message.replaceAll(pattern, replacerValue);
        }

        Mode mode = getMode();
        if (mode == Mode.BLOCK && message.equalsIgnoreCase(checkedMessage)) return message;

        Config.sendMessage(sender, "ads_warn");
        Tools.dispatch(section.getStringList("commands"), sender.getName());
        return mode == Mode.BLOCK ? null : checkedMessage;
    }

    public enum Mode {
        BLOCK,
        REPLACE
    }
}

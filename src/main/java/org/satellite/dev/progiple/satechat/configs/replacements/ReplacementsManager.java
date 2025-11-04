package org.satellite.dev.progiple.satechat.configs.replacements;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;

@UtilityClass
public class ReplacementsManager {
    private ReplacementsConfig config;

    public void initialize() {
        config = new ReplacementsConfig();
    }

    public void reload() {
        config.reload();
    }

    public String replace(CommandSender sender, String message) {
        return config.replace(sender, message);
    }
}

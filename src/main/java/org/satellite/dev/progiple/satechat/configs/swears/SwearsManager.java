package org.satellite.dev.progiple.satechat.configs.swears;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.satellite.dev.progiple.satechat.configs.Config;

@UtilityClass
public class SwearsManager {
    private ISwearsConfig config;

    public void initialize() {
        config = Config.optimizedStorage() ? new OptimizedSwearsConfig() : new SwearsConfig();
    }

    public void reload() {
        if (config.isOptimized() != Config.optimizedStorage()) initialize();
        else config.reload();
    }

    public String replace(CommandSender sender, String message) {
        return config.process(sender, message);
    }
}

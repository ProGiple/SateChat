package org.satellite.dev.progiple.satechat.configs.ads;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.satellite.dev.progiple.satechat.configs.Config;

import javax.annotation.Nullable;

@UtilityClass
public class AdsManager {
    private IAdsConfig config;

    public void initialize() {
        config = Config.optimizedStorage() ? new OptimizedAdsConfig() : new AdsConfig();
    }

    public void reload() {
        if (config.isOptimized() != Config.optimizedStorage()) initialize();
        else config.reload();
    }

    public @Nullable String block(CommandSender sender, String message) {
        return config.process(sender, message);
    }
}

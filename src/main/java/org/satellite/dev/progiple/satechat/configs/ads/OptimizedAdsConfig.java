package org.satellite.dev.progiple.satechat.configs.ads;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.List;

@Getter
public class OptimizedAdsConfig extends AdsConfig {
    private final boolean enabled;
    private final boolean optimized = true;
    private final List<String> actionCommands;
    private final List<String> formats;
    private final List<String> whitelist;
    private final Mode mode;
    public OptimizedAdsConfig() {
        ConfigurationSection section = Config.getSection("advertisement_block");
        this.enabled = section.getBoolean("enable");
        this.actionCommands = section.getStringList("commands");

        this.formats = super.getFormats();
        this.whitelist = super.getWhitelist();
        this.mode = super.getMode();
    }
}

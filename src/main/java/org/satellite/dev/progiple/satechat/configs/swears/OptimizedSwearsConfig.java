package org.satellite.dev.progiple.satechat.configs.swears;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.List;

@Getter
public class OptimizedSwearsConfig extends SwearsConfig {
    private final boolean optimized = true;
    private final boolean enabled;
    private final List<String> actionCommands;
    private final List<String> list;
    private final Mode mode;
    public OptimizedSwearsConfig() {
        ConfigurationSection section = Config.getSection("swear_block");
        this.enabled = section.getBoolean("enable");
        this.actionCommands = section.getStringList("commands");

        this.list = super.getList();
        this.mode = super.getMode();
    }
}

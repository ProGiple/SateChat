package org.satellite.dev.progiple.satechat.configs.swears;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.List;

public class OptimizedSwearsConfig extends SwearsConfig {
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

    @Override
    public boolean isOptimized() {
        return true;
    }

    @Override
    public List<String> getActionCommands() {
        return actionCommands;
    }

    @Override
    public List<String> getList() {
        return list;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

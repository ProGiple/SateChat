package org.satellite.dev.progiple.satechat.configs.swears;

import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.FileConfig;

import java.util.Comparator;
import java.util.List;

public class SwearsConfig extends FileConfig implements ISwearsConfig {
    public SwearsConfig() {
        super("swear_block");
    }

    @Override
    public List<String> getList() {
        return this.getStringList("list")
                .stream()
                .map(String::toLowerCase)
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList();
    }

    @Override
    public Mode getMode() {
        return Utils.getEnumValue(Mode.class, this.getString("replacement_mode"), Mode.START_WITH_END);
    }

    @Override
    public boolean isOptimized() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return Config.getBoolean("swear_block.enable");
    }

    @Override
    public List<String> getActionCommands() {
        return Config.getList("swear_block.commands");
    }
}

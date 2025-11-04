package org.satellite.dev.progiple.satechat.configs.ads;

import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.configs.FileConfig;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.List;

public class AdsConfig extends FileConfig implements IAdsConfig {
    public AdsConfig() {
        super("advertisement_block");
    }

    @Override
    public boolean isOptimized() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return Config.getBoolean("advertisement_block.enable");
    }

    @Override
    public List<String> getActionCommands() {
        return Config.getList("advertisement_block.commands");
    }

    @Override
    public List<String> getFormats() {
        return this.getStringList("formats");
    }

    @Override
    public List<String> getWhitelist() {
        return this.getStringList("whitelist");
    }

    @Override
    public Mode getMode() {
        return Utils.getEnumValue(Mode.class, this.getString("mode"), Mode.BLOCK);
    }
}

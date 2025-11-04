package org.satellite.dev.progiple.satechat.configs;

import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.satechat.SateChat;

public abstract class FileConfig extends IConfig {
    public FileConfig(String configId) {
        super(SateChat.getINSTANCE().getDataFolder(), Config.getString(configId + ".file"));
    }
}

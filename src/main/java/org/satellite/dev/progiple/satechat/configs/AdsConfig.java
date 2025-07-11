package org.satellite.dev.progiple.satechat.configs;

import lombok.Setter;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.satechat.SateChat;

import java.io.File;
import java.util.List;

public class AdsConfig {
    @Setter
    private static AdsConfig adsConfig;

    public static AdsConfig get() {
        return adsConfig;
    }

    private final IConfig config;
    public AdsConfig(String path) {
        File file = new File(SateChat.getINSTANCE().getDataFolder(), path);
        this.config = new IConfig(file);
    }

    public List<String> getFormats() {
        return this.config.getStringList("formats");
    }

    public List<String> getWhitelist() {
        return this.config.getStringList("whitelist");
    }

    public void reload() {
        this.config.reload();
    }
}

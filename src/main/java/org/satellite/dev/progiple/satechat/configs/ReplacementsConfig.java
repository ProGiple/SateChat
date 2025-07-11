package org.satellite.dev.progiple.satechat.configs;

import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.satechat.SateChat;

import java.io.File;

public class ReplacementsConfig {
    @Setter
    private static ReplacementsConfig replacementsConfig;

    public static ReplacementsConfig get() {
        return replacementsConfig;
    }

    private final IConfig config;
    public ReplacementsConfig(String path) {
        File file = new File(SateChat.getINSTANCE().getDataFolder(), path);
        this.config = new IConfig(file);
    }

    public ConfigurationSection getSection() {
        return this.config.getSection("list");
    }

    public void reload() {
        this.config.reload();
    }
}

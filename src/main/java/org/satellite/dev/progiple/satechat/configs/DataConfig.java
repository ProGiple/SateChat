package org.satellite.dev.progiple.satechat.configs;

import lombok.Getter;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.satellite.dev.progiple.satechat.SateChat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataConfig {
    private final Configuration config;
    @Getter private final UUID uuid;
    public DataConfig(UUID uuid) {
        this.uuid = uuid;

        this.config = new Configuration(SateChat.getINSTANCE().getDataFolder(), String.format("data/%s.yml", uuid.toString()));
        if (this.config.getFile().exists()) return;

        this.config.setBoolean("spy_mode", false);
        this.config.setBoolean("ignore_all", false);
        this.config.setStringList("ignored", new ArrayList<>());
        this.config.save();
    }

    public boolean switchSpyMode() {
        boolean newState = !this.config.getBoolean("spy_mode");
        this.config.setBoolean("spy_mode", newState);
        this.config.save();
        return newState;
    }

    public List<String> getIgnored() {
        return this.config.getStringList("ignored");
    }

    public boolean switchIgnore(String playerName) {
        List<String> list = this.getIgnored();
        boolean newState = !list.contains(playerName);

        if (newState) list.add(playerName);
        else list.remove(playerName);

        this.config.setStringList("ignored", list);
        this.config.save();
        return newState;
    }

    public boolean switchIgnore() {
        boolean newState = !this.config.getBoolean("ignore_all");
        this.config.setBoolean("ignore_all", newState);
        this.config.save();
        return newState;
    }

    public boolean getBool(String path) {
        return config.getBoolean(path);
    }
}

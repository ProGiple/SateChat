package org.satellite.dev.progiple.satechat.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.satechat.SateChat;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class SwearsConfig {
    @Setter
    private static SwearsConfig swearsConfig;

    public static SwearsConfig get() {
        return swearsConfig;
    }

    private final IConfig config;
    public SwearsConfig(String path) {
        File file = new File(SateChat.getINSTANCE().getDataFolder(), path);
        this.config = new IConfig(file);
    }

    public List<String> getList() {
        return this.config.getStringList("list")
                .stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList();
    }

    public boolean disableTranslates() {
        return this.config.getBoolean("disable_translates");
    }

    public Mode getMode() {
        return Mode.valueOf(this.config.getString("replacement_mode"));
    }

    public void reload() {
        this.config.reload();
    }

    public enum Mode {
        START_WITH_END,
        FUNTIME,
        FULL,
        ONLY_START,
        ONLY_END;
    }
}

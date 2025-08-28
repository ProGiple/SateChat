package org.satellite.dev.progiple.satechat.configs;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.satechat.SateChat;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

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
                .map(String::toLowerCase)
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

    @AllArgsConstructor
    public enum Mode {
        START_WITH_END(s -> s.charAt(0) + "*".repeat(Math.max(s.length() - 2, 0)) + s.charAt(s.length() - 1)),
        FUNTIME(s -> s.charAt(0) + "*" + s.charAt(s.length() - 1)),
        FULL(s -> "*".repeat(s.length())),
        ONLY_START(s -> s.charAt(0) + "*".repeat(s.length() - 1)),
        ONLY_END(s -> "*".repeat(s.length() - 1) + s.charAt(s.length() - 1));

        public final Function<String, String> function;

        public String replace(String line) {
            return this.function.apply(line);
        }
    }
}

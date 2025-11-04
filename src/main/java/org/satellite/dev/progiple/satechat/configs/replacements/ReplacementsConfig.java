package org.satellite.dev.progiple.satechat.configs.replacements;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.FileConfig;
import org.satellite.dev.progiple.satechat.utils.Tools;
import org.satellite.dev.progiple.satechat.utils.Translit;

import java.util.HashMap;
import java.util.Map;

public class ReplacementsConfig extends FileConfig {
    private final Map<String, String> replacements = new HashMap<>();
    private boolean isEnabled;
    public ReplacementsConfig() {
        super("replacement_words");
        this.initialize();
    }

    private void initialize() {
        this.isEnabled = Config.getBoolean("replacement_words.enable");
        ConfigurationSection section = this.getSection("list");

        this.replacements.clear();
        for (String key : section.getKeys(false)) {
            String[] split = section.getString(key + ".words").split(", ");

            String value = ColorManager.color(section.getString(key + ".replacement"));
            for (String s : split) {
                this.replacements.put(s.toLowerCase(), value);
            }
        }
    }

    @Override
    public void reload() {
        super.reload();
        this.initialize();
    }

    public String replace(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "replacements") || !this.isEnabled) return message;

        String normalized = message.toLowerCase();
        String translited = Translit.process(normalized).trim();
        for (String word : this.replacements.keySet()) {
            if (word == null || word.isEmpty()) continue;

            if (normalized.contains(word) || translited.contains(word)) {
                String replacement = this.replacements.get(word);
                return replacement != null ? replacement : message;
            }
        }

        return message;
    }
}

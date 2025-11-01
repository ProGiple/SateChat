package org.satellite.dev.progiple.satechat.configs;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.Translit;

@UtilityClass
public class ReplacementsConfig {
    private IConfig config;

    public void initialize(SateChat sateChat) {
        config = new IConfig(sateChat.getDataFolder(), Config.getString("replacement_words.file"));
    }

    public ConfigurationSection getSection() {
        return config.getSection("list");
    }

    public void reload() {
        config.reload();
    }

    public String replace(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "replacements")) return message;

        ConfigurationSection section = Config.getSection("replacement_words");
        if (!section.getBoolean("enable")) return message;

        ConfigurationSection rplSection = ReplacementsConfig.getSection();
        if (rplSection == null) return message;

        String normalized = Translit.process(message).toLowerCase().trim();
        for (String key : rplSection.getKeys(false)) {
            String rawWords = rplSection.getString(key + ".words");
            if (rawWords == null || rawWords.isEmpty()) continue;

            String[] words = rawWords.split(",\\s*");
            for (String word : words) {
                if (normalized.contains(word.toLowerCase())) {
                    String replacement = rplSection.getString(key + ".replacement");
                    return replacement != null ? ColorManager.color(replacement) : message;
                }
            }
        }

        return message;
    }

}

package org.satellite.dev.progiple.satechat.configs;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.Translit;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@UtilityClass
public class SwearsConfig {
    private IConfig config;

    public void initialize(SateChat sateChat) {
        config = new IConfig(sateChat.getDataFolder(), Config.getString("swear_block.file"));
    }

    public List<String> getList() {
        return config.getStringList("list")
                .stream()
                .map(String::toLowerCase)
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList();
    }

    public Mode getMode() {
        return Utils.getEnumValue(Mode.class, config.getString("replacement_mode"), Mode.START_WITH_END);
    }

    public void reload() {
        config.reload();
    }

    public String replace(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "swears")) return message;

        ConfigurationSection section = Config.getSection("swear_block");
        if (!section.getBoolean("enable")) return message;

        List<String> swears = SwearsConfig.getList();
        SwearsConfig.Mode mode = SwearsConfig.getMode();
        if (swears.isEmpty()) return message;

        boolean hasSwear = false;
        String[] words = message.split("\\s+");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            String lowered = word.toLowerCase();
            String translit = Translit.process(lowered);

            String cleaned = word.replaceAll("[^a-zA-Zа-яА-ЯёЁ]", "");
            String cleanedLowered = cleaned.toLowerCase();
            String translitCleaned = Translit.process(cleanedLowered);

            boolean matched = swears.stream().anyMatch(swear ->
                    lowered.contains(swear) || translit.contains(swear) ||
                            (!cleanedLowered.isEmpty() && (cleanedLowered.contains(swear) || translitCleaned.contains(swear)))
            );

            if (matched) {
                String replacement = mode.replace(cleaned.isEmpty() ? word : cleaned);
                result.append(replacement).append(" ");
                hasSwear = true;
            } else {
                result.append(word).append(" ");
            }
        }

        if (hasSwear) {
            Bukkit.getScheduler().runTaskLater(SateChat.getINSTANCE(), () -> {
                Config.sendMessage(sender, "swear_warn");
                Tools.dispatch(section.getStringList("commands"), sender.getName());
            }, 2L);
        }

        return result.toString().trim();
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

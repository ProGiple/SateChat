package org.satellite.dev.progiple.satechat.configs.swears;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.utils.Tools;
import org.satellite.dev.progiple.satechat.utils.Translit;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.IFileConfig;

import java.util.List;
import java.util.function.Function;

public interface ISwearsConfig extends IFileConfig {
    List<String> getList();
    Mode getMode();
    void reload();
    default String process(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "swears") || !isEnabled()) return message;

        List<String> swears = getList();
        Mode mode = getMode();
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
                Tools.dispatch(getActionCommands(), sender.getName());
            }, 2L);
        }

        return result.toString().trim();
    }

    @AllArgsConstructor
    enum Mode {
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

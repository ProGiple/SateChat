package org.satellite.dev.progiple.satechat;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.satellite.dev.progiple.satechat.configs.AdsConfig;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.ReplacementsConfig;
import org.satellite.dev.progiple.satechat.configs.SwearsConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class Tools {
    public boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission) || sender.hasPermission("satechat.*");
    }

    public boolean hasBypassPermission(CommandSender sender, String bypass) {
        return hasPermission(sender, "satechat.bypass." + bypass) || sender.hasPermission("satechat.bypass.*");
    }

    public String useColor(CommandSender sender, String message) {
        if (!Tools.hasPermission(sender, "satechat.useColor")) message = ChatColor.stripColor(message);
        return ColorManager.color(message);
    }

    public void dispatch(List<String> commands, String senderName) {
        commands.forEach(l -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), l
                .replace("{sender}", senderName)
                .replace("[sender]", senderName)));
    }

    public boolean spamBlock(String latestMessage, CommandSender sender, String isMessage) {
        if (Tools.hasBypassPermission(sender, "spam")) return false;

        ConfigurationSection section = Config.getSection("spam_block");
        if (!section.getBoolean("enable")) return false;

        if (latestMessage != null && latestMessage.equalsIgnoreCase(isMessage)) {
            Config.sendMessage(sender, "spam_warn");
            Tools.dispatch(section.getStringList("commands"), sender.getName());
            return true;
        }
        return false;
    }

    public String swearReplacement(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "swears")) return message;

        ConfigurationSection section = Config.getSection("swear_block");
        if (!section.getBoolean("enable")) return message;

        SwearsConfig config = SwearsConfig.get();
        if (config == null) return message;
        
        List<String> swears = config.getList();
        SwearsConfig.Mode mode = config.getMode();

        boolean hasOne = false;
        for (String part : message.split(" ")) {
            String lowed = part.toLowerCase();
            String transliteLowed = Translit.process(lowed);

            String cleanedString = part.replaceAll("[^a-zA-Zа-яА-ЯёЁ]", "");
            String cleanedLowed = cleanedString.toLowerCase();
            String transliteCleaned = Translit.process(cleanedLowed);
            for (String swear : swears) {
                if (lowed.contains(swear) || transliteLowed.contains(swear)) {
                    message = message.replace(part, mode.replace(part));
                    hasOne = true;
                    continue;
                }

                if (!cleanedLowed.isEmpty() && (cleanedLowed.contains(swear) || transliteCleaned.contains(swear))) {
                    message = message.replace(part, mode.replace(cleanedString));
                    hasOne = true;
                }
            }
        }

        if (hasOne) {
            Bukkit.getScheduler().runTaskLater(SateChat.getINSTANCE(), () -> {
                Config.sendMessage(sender, "swear_warn");
                Tools.dispatch(section.getStringList("commands"), sender.getName());
            }, 2L);
        }

        return message;
    }

    public boolean capsBlock(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "caps")) return false;

        ConfigurationSection section = Config.getSection("caps_block");
        if (!section.getBoolean("enable") || message.length() < section.getInt("min_letters_for_check")) return false;

        double percent = (double) section.getInt("min_caps_letter_percent") / 100;
        message = ChatColor.stripColor(message.replace(" ", ""));
        if ((double) message.chars().filter(Character::isUpperCase).count() / message.length() >= percent) {
            Config.sendMessage(sender, "caps_warn");
            Tools.dispatch(section.getStringList("commands"), sender.getName());
            return true;
        }
        return false;
    }

    public boolean adsBlocks(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "ads")) return false;

        ConfigurationSection section = Config.getSection("advertisement_block");
        if (!section.getBoolean("enable")) return false;

        AdsConfig adsConfig = AdsConfig.get();
        List<String> patterns = adsConfig.getFormats();

        String checkedMessage = message;
        for (String s : adsConfig.getWhitelist()) checkedMessage = checkedMessage.replace(s, "");

        String finalCheckedMessage = checkedMessage;
        if (patterns.stream().anyMatch(s -> Pattern.compile(s).matcher(finalCheckedMessage).find())) {
            Config.sendMessage(sender, "ads_warn");
            Tools.dispatch(section.getStringList("commands"), sender.getName());
            return true;
        }
        return false;
    }

    public String replacementWords(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "replacements")) return message;

        ConfigurationSection section = Config.getSection("replacement_words");
        if (!section.contains("enable")) return message;

        ReplacementsConfig rplConfig = ReplacementsConfig.get();
        ConfigurationSection rplSection = rplConfig.getSection();

        String finalNormalized = Translit.process(message);
        String key = rplSection.getKeys(false)
                .stream()
                .filter(k -> {
                    String[] split = Objects.requireNonNull(rplSection.getString(k + ".words")).split(", ");
                    return Arrays.stream(split).anyMatch(w -> finalNormalized.toLowerCase().contains(w.toLowerCase()));
                })
                .findFirst()
                .orElse(null);
        if (key == null || key.isEmpty()) return message;

        return ColorManager.color(Objects.requireNonNull(rplSection.getString(key + ".replacement")));
    }

    public String replacementCommands(String message, boolean isClickable) {
        ConfigurationSection section = Config.getSection("command_replacements");
        if (!section.getBoolean("enable")) return message;

        String format = section.getString(isClickable ? "clickable_format" : "format");
        if (format == null || format.isEmpty()) return message;

        if (!isClickable) format = ColorManager.color(format);

        Pattern pattern = Pattern.compile("(?<!\\S)/(\\w+)");
        Matcher matcher = pattern.matcher(message);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String command = matcher.group(1);

            String formatted = format.replace("[command]", command);
            matcher.appendReplacement(result, Matcher.quoteReplacement(formatted));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}

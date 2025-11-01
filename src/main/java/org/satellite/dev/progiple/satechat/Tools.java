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

import javax.annotation.Nullable;
import java.util.List;
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
        if (!Tools.hasPermission(sender, "satechat.useColor")) return ChatColor.stripColor(message);
        return ColorManager.color(message);
    }

    public void dispatch(List<String> commands, String senderName) {
        commands.forEach(l -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), l.replace("[sender]", senderName)));
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

//    public String swearReplacement(CommandSender sender, String message) {
//        if (Tools.hasBypassPermission(sender, "swears")) return message;
//
//        ConfigurationSection section = Config.getSection("swear_block");
//        if (!section.getBoolean("enable")) return message;
//
//        List<String> swears = SwearsConfig.getList();
//        SwearsConfig.Mode mode = SwearsConfig.getMode();
//
//        boolean hasOne = false;
//        for (String part : message.split(" ")) {
//            String lowed = part.toLowerCase();
//            String transliteLowed = Translit.process(lowed);
//
//            String cleanedString = part.replaceAll("[^a-zA-Zа-яА-ЯёЁ]", "");
//            String cleanedLowed = cleanedString.toLowerCase();
//            String transliteCleaned = Translit.process(cleanedLowed);
//            for (String swear : swears) {
//                if (lowed.contains(swear) || transliteLowed.contains(swear)) {
//                    message = message.replace(part, mode.replace(part));
//                    hasOne = true;
//                    continue;
//                }
//
//                if (!cleanedLowed.isEmpty() && (cleanedLowed.contains(swear) || transliteCleaned.contains(swear))) {
//                    message = message.replace(part, mode.replace(cleanedString));
//                    hasOne = true;
//                }
//            }
//        }
//
//        if (hasOne) {
//            Bukkit.getScheduler().runTaskLater(SateChat.getINSTANCE(), () -> {
//                Config.sendMessage(sender, "swear_warn");
//                Tools.dispatch(section.getStringList("commands"), sender.getName());
//            }, 2L);
//        }
//
//        return message;
//    }

    public String capsBlock(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "caps")) return message;

        ConfigurationSection section = Config.getSection("caps_block");
        if (!section.getBoolean("enable") || message.length() < section.getInt("min_letters_for_check")) return message;

        double percent = (double) section.getInt("min_caps_letter_percent") / 100;
        String checkedMessage = ChatColor.stripColor(message.replace(" ", ""));
        if ((double) checkedMessage.chars().filter(Character::isUpperCase).count() / checkedMessage.length() >= percent) {
            Config.sendMessage(sender, "caps_warn");
            Tools.dispatch(section.getStringList("commands"), sender.getName());

            boolean fullBlockSend = section.getBoolean("full_block_send");
            return fullBlockSend ? null : message.toLowerCase();
        }

        return message;
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

    public boolean allBlocks(CommandSender sender, String starterMessage, @Nullable String latestMessage) {
        return sender == null
                || (latestMessage != null && Tools.spamBlock(latestMessage, sender, starterMessage))
                || AdsConfig.block(sender, starterMessage) == null
                || Tools.capsBlock(sender, starterMessage) == null;
    }

    public String allReplacements(CommandSender sender, String message, boolean isClickable) {
        message = SwearsConfig.replace(sender, message);
        message = ReplacementsConfig.replace(sender, message);
        message = Tools.replacementCommands(message, isClickable);

        String adsReplacer = AdsConfig.block(sender, message);
        message = adsReplacer == null ? message : adsReplacer;

        String capsReplacer = Tools.capsBlock(sender, message);
        return capsReplacer == null ? message : capsReplacer;
    }
}

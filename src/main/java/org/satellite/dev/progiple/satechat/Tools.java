package org.satellite.dev.progiple.satechat;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.List;
import java.util.Objects;

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

    public boolean swearBlock(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "swears")) return false;

        ConfigurationSection section = Config.getSection("swear_block");
        if (!section.getBoolean("enable")) return false;

        List<String> swears = section.getStringList("words");
        if (swears.stream().anyMatch(l -> message.toLowerCase().contains(l.toLowerCase()))) {
            Config.sendMessage(sender, "swear_warn");
            Tools.dispatch(section.getStringList("commands"), sender.getName());
            return true;
        }
        return false;
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

        List<String> words = section.getStringList("key_words");
        if (words.stream().anyMatch(l -> message.toLowerCase().contains(l.toLowerCase()))) {
            Config.sendMessage(sender, "ads_warn");
            Tools.dispatch(section.getStringList("commands"), sender.getName());
            return true;
        }
        return false;
    }

    public String replacementWords(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "replacements")) return message;
        ConfigurationSection section = Config.getSection("replacement_words");

        String key = section.getKeys(false)
                .stream()
                .filter(k -> message
                        .toLowerCase()
                        .contains(Objects.requireNonNull(Objects.requireNonNull(section.getString(k + ".word")).toLowerCase())))
                .findFirst()
                .orElse(null);
        if (key == null || key.isEmpty()) return message;

        return ColorManager.color(Objects.requireNonNull(section.getString(key + ".replacement")));
    }

    public boolean useHelpMessages(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "helpmessages")) return false;
        ConfigurationSection section = Config.getSection("help_messages");

        String key = section.getKeys(false)
                .stream()
                .filter(k -> message
                        .toLowerCase()
                        .contains(Objects.requireNonNull(Objects.requireNonNull(section.getString(k + ".word")).toLowerCase())))
                .findFirst()
                .orElse(null);
        if (key == null || key.isEmpty()) return false;

        Tools.dispatch(section.getStringList(key + ".commands"), sender.getName());
        return section.getBoolean(key + ".disable_send_original");
    }
}

package org.satellite.dev.progiple.satechat.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.novasparkle.lunaspring.API.util.utilities.Cache;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.ads.AdsManager;
import org.satellite.dev.progiple.satechat.configs.replacements.ReplacementsManager;
import org.satellite.dev.progiple.satechat.configs.swears.SwearsManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
            Bukkit.getScheduler().runTaskLater(SateChat.getINSTANCE(), () -> {
                Config.sendMessage(sender, "spam_warn");
                Tools.dispatch(section.getStringList("commands"), sender.getName());
            }, 2L);
            return true;
        }
        return false;
    }

    public String capsBlock(CommandSender sender, String message) {
        if (Tools.hasBypassPermission(sender, "caps")) return message;

        ConfigurationSection section = Config.getSection("caps_block");
        if (!section.getBoolean("enable") || message.length() < section.getInt("min_letters_for_check")) return message;

        double percent = (double) section.getInt("min_caps_letter_percent") / 100;
        String checkedMessage = ChatColor.stripColor(message.replace(" ", ""));
        if ((double) checkedMessage.chars().filter(Character::isUpperCase).count() / checkedMessage.length() >= percent) {
            if (!CacheValue.CAPS.has(sender)) {
                Bukkit.getScheduler().runTaskLater(SateChat.getINSTANCE(), () -> {
                    Config.sendMessage(sender, "caps_warn");
                    Tools.dispatch(section.getStringList("commands"), sender.getName());
                }, 2L);
            }

            CacheValue.CAPS.push(sender);
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

        Pattern pattern = Pattern.compile("(?<!\\S)/(\\w+(?:-\\w+)*)");
        Matcher matcher = pattern.matcher(message);

        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String raw = matcher.group(1);

            String[] parts = raw.split("-");
            String command = parts[0];
            String args = parts.length > 1
                    ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length))
                    : "";

            String formatted = format
                    .replace("[command]", command)
                    .replace("[args]", args.isEmpty() ? "" : " " + args);

            matcher.appendReplacement(result, Matcher.quoteReplacement(formatted));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public boolean allBlocks(CommandSender sender, String starterMessage, @Nullable String latestMessage) {
        return sender == null
                || (latestMessage != null && Tools.spamBlock(latestMessage, sender, starterMessage))
                || AdsManager.block(sender, starterMessage) == null
                || Tools.capsBlock(sender, starterMessage) == null;
    }

    public String allReplacements(CommandSender sender, String message, boolean isClickable) {
        message = SwearsManager.replace(sender, message);
        message = ReplacementsManager.replace(sender, message);
        message = Tools.replacementCommands(message, isClickable);

        String adsReplacer = AdsManager.block(sender, message);
        message = adsReplacer == null ? message : adsReplacer;

        String capsReplacer = Tools.capsBlock(sender, message);
        return capsReplacer == null ? message : capsReplacer;
    }

    public String mention(Collection<? extends Player> collection, String message, String sound, String mentionSymbol, boolean isClickable) {
        ConfigurationSection section = Config.getSection("mentions");
        if (!section.getBoolean("enable")) return message;

        String format = section.getString(isClickable ? "clickable_format" : "format");
        if (format == null) return message;

        for (Player player : collection) {
            String playerName = player.getName();

            String value = String.format(" %s%s ", mentionSymbol, playerName);
            if (message.contains(value)) {
                AnnounceUtils.sound(player, sound);
                message = message.replace(value, format.replace("[mentioned]", playerName));
            }
        }
        return isClickable ? message : ColorManager.color(message);
    }

    public String mention(Collection<? extends Player> collection, String message, String sound, boolean isClickable) {
        return mention(collection, message, sound, Config.getString("mentions.symbol"), isClickable);
    }

    public String mention(Collection<? extends Player> collection, String message, boolean isClickable) {
        return mention(collection, message, Config.getString("mentions.sound"), isClickable);
    }

    public boolean hasMention(CommandSender mentioned, String message, String mentionChar) {
        return message.contains(" " + mentionChar + mentioned.getName() + " ");
    }

    public boolean hasMention(CommandSender mentioned, String message) {
        return hasMention(mentioned, message, Config.getString("mentions.symbol"));
    }

    public enum CacheValue {
        CAPS,
        ADS;

        private final Cache<CommandSender, Boolean> cache = new Cache<>(100, TimeUnit.MILLISECONDS, 125);

        public void push(CommandSender sender) {
            cache.put(sender, true);
        }

        public boolean has(CommandSender sender) {
            return cache.getIfPresent(sender) != null;
        }
    }
}

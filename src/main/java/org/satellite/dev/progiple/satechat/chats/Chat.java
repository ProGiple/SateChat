package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class Chat {
    private final LunaPlugin lunaPlugin;
    private final String id;
    private final String format;
    private final int range;
    private final CooldownPrevent<UUID> cooldownPrevent;
    private final Map<UUID, String> latestMessage = new HashMap<>();
    private final char symbol;
    public Chat(LunaPlugin lunaPlugin, String id, String format, int range, int cooldownTicks, char starterSymbol) {
        this.lunaPlugin = lunaPlugin;
        this.id = id;
        this.format = format;
        this.range = range;
        this.cooldownPrevent = new CooldownPrevent<>(cooldownTicks * 50);
        this.symbol = starterSymbol;
    }

    public Chat(LunaPlugin lunaPlugin, String id, String format) {
        this(lunaPlugin, id, format, -1, 0, ' ');
    }

    public Chat(LunaPlugin lunaPlugin, ConfigurationSection section) {
        this.lunaPlugin = lunaPlugin;
        this.id = section.getName();
        this.range = section.getKeys(false).contains("range") ? section.getInt("range") : -1;

        String format = section.getString("format");
        this.format = format != null && !format.isEmpty() ? format : "%player_name% -> {message}";

        String symbol = section.getString("symbol");
        this.symbol = symbol != null && !symbol.isEmpty() ? symbol.charAt(0) : ' ';

        int cooldownTicks = section.getKeys(false).contains("cooldownTicks") ? section.getInt("cooldownTicks") : 0;
        this.cooldownPrevent = new CooldownPrevent<>(cooldownTicks * 50);
    }

    public boolean sendMessage(UUID sender, final String starterMessage) {
        Player player = Bukkit.getPlayer(sender);
        if (player == null
                || Tools.spamBlock(this.latestMessage.get(sender), player, starterMessage)
                || Tools.adsBlocks(player, starterMessage)
                || Tools.swearBlock(player, starterMessage)
                || Tools.capsBlock(player, starterMessage)
                || Tools.useHelpMessages(player, starterMessage)) return false;

        if ((!Tools.hasBypassPermission(player, "cooldown") && this.cooldownPrevent.isCancelled(null, sender))) {
            Config.sendMessage(player, "chatCooldown", String.valueOf(
                    (this.cooldownPrevent.getCooldownMap().get(sender) - System.currentTimeMillis()) / 1000L));
            return false;
        }
        this.latestMessage.put(sender, starterMessage);

        Collection<? extends Player> collection = this.getMessageViewers(sender);
        String message = this.mention(collection, Tools.useColor(player, starterMessage));

        if (this.symbol != ' ') message = message.substring(1);
        message = Tools.replacementWords(player, message);

        String strSound = Config.getString("mentions.sound");
        String endedMessage = ColorManager.color(Utils.setPlaceholders(player, this.format))
                .replace("{message}", message)
                .replace("[message]", message);

        collection.forEach(p -> {
            p.sendMessage(endedMessage);
            if (this.hasMention(p, starterMessage)) AnnounceUtils.sound(p, strSound);
        });
        SateChat.getINSTANCE().getLogger().log(Level.INFO, endedMessage);

        return true;
    }

    public boolean hasMention(CommandSender mentioned, String message) {
        String mentionString = Config.getString("mentions.symbol");
        return message.contains(mentionString + mentioned.getName());
    }

    public String mention(Collection<? extends Player> collection, String message) {
        String mentionString = Config.getString("mentions.symbol");
        String format = Config.getString("mentions.in_chat_format");
        for (Player player : collection) {
            message = message.replace(mentionString + player.getName(), format
                            .replace("{mentioned}", player.getName())
                            .replace("[mentioned]", player.getName()));
        }
        return ColorManager.color(message);
    }

    public Collection<? extends Player> getMessageViewers(UUID sender) {
        if (this.range > 0) {
            Player player = Bukkit.getPlayer(sender);
            return player == null ? null : player
                    .getWorld()
                    .getPlayers()
                    .stream()
                    .filter(p -> p.getLocation().distance(player.getLocation()) <= this.range)
                    .collect(Collectors.toList());
        } else return Bukkit.getOnlinePlayers();
    }
}

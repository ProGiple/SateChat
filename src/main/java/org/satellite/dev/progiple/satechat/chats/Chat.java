package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class Chat extends RawChat{
    private final Map<UUID, String> latestMessage = new HashMap<>();
    public Chat(LunaPlugin lunaPlugin, ChatSettings settings) {
        super(lunaPlugin, settings);
    }

    @Override
    public boolean sendMessage(IChatUser sender, final String starterMessage) {
        Player player = sender.getPlayer();
        if (player == null
                || Tools.spamBlock(this.latestMessage.get(sender.getUUID()), player, starterMessage)
                || Tools.adsBlocks(player, starterMessage)
                || Tools.capsBlock(player, starterMessage)) return false;

        if ((!Tools.hasBypassPermission(player, "cooldown") && this.getCooldownPrevent().isCancelled(null, sender))) {
            Config.sendMessage(player, "chatCooldown", String.valueOf(
                    (this.getCooldownPrevent().getCooldownMap().get(sender) - System.currentTimeMillis()) / 1000L));
            return false;
        }
        this.latestMessage.put(sender.getUUID(), starterMessage);

        Collection<? extends Player> collection = this.getMessageViewers(sender);
        String message = this.mention(collection, Tools.useColor(player, starterMessage));

        if (this.getSettings().getSymbol() != ' ') message = message.substring(1);
        message = Tools.swearReplacement(player, message);
        message = Tools.replacementWords(player, message);
        message = Tools.replacementCommands(message);

        String strSound = Config.getString("mentions.sound");
        String endedMessage = ColorManager.color(Utils.setPlaceholders(player, this.getSettings().getFormat()))
                .replace("{message}", message)
                .replace("[message]", message);

        collection.forEach(p -> {
            p.sendMessage(endedMessage);
            if (this.hasMention(p, starterMessage)) AnnounceUtils.sound(p, strSound);
        });

        SateChat.getINSTANCE().getLogger().log(Level.INFO, endedMessage);
        return true;
    }

    @Override
    public boolean hasMention(CommandSender mentioned, String message) {
        String mentionString = Config.getString("mentions.symbol");
        return message.contains(mentionString + mentioned.getName());
    }

    @Override
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

    @Override
    public Collection<? extends Player> getMessageViewers(IChatUser sender) {
        Player player = sender.getPlayer();
        if (player == null) return null;

        Collection<Player> players = player.getWorld().getPlayers();
        if (this.getSettings().getRange() > 0) {
            return players
                    .stream()
                    .filter(p -> p.getLocation().distance(player.getLocation()) <= this.getSettings().getRange())
                    .collect(Collectors.toList());
        } else return players;
    }

    @Override
    public boolean isBlocked(IChatUser chatUser) {
        Player player = chatUser.getPlayer();
        return player == null || !Tools.hasPermission(player, "satechat.use." + this.getSettings().getId());
    }
}

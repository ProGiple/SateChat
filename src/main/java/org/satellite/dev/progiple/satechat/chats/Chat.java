package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.novasparkle.lunaspring.API.util.utilities.ComponentUtils;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.AfterSendChatEvent;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class Chat extends RawChat {
    private final Map<UUID, String> latestMessage = new HashMap<>();
    public Chat(LunaPlugin lunaPlugin, ChatSettings settings) {
        super(lunaPlugin, settings);
    }

    @Override
    public boolean sendMessage(IChatUser sender, final String starterMessage) {
        Player player = sender.getPlayer();
        if (Tools.allBlocks(player, starterMessage, this.latestMessage.get(sender.getUUID()))) return false;

        if ((!Tools.hasBypassPermission(player, "cooldown") && this.getCooldownPrevent().isCancelled(null, sender))) {
            long value = this.getCooldownPrevent().getRemaining(sender);
            Config.sendMessage(player, "chatCooldown", String.valueOf(LunaMath.round((double) value / 1000L, 1)));
            return false;
        }

        boolean isClickable = this.getSettings().isClickable();
        this.latestMessage.put(sender.getUUID(), starterMessage);

        Collection<? extends Player> viewers = this.getMessageViewers(sender);
        String message = this.mention(viewers, Tools.useColor(player, starterMessage));

        if (this.getSettings().getSymbol() != ' ') message = message.substring(1);
        message = Tools.allReplacements(player, message, isClickable);

        String strSound = Config.getString("mentions.sound");
        String endedMessage = Utils.setPlaceholders(player, this.getSettings().getFormat()).replace("[message]", message);
        endedMessage = ColorManager.color(endedMessage.replace("[sender]", player.getName()));

        String finalEndedMessage = endedMessage;
        viewers.forEach(p -> {
            if (isClickable) p.spigot().sendMessage(ComponentUtils.createClickableText(finalEndedMessage, ClickEvent.Action.SUGGEST_COMMAND));
            else p.sendMessage(finalEndedMessage);

            if (this.hasMention(p, starterMessage)) AnnounceUtils.sound(p, strSound);
        });

        String logging = Utils.setPlaceholders(player, this.getSettings().getLogger()
                .replace("[sender]", player.getName())
                .replace("[message]",message));
        SateChat.getINSTANCE().getLogger().log(Level.INFO, logging);

        if (Config.useEvents()) {
            AfterSendChatEvent afterSendChatEvent = new AfterSendChatEvent(player, sender, this, finalEndedMessage, viewers);
            afterSendChatEvent.callEvent();
        }

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
        boolean isClicable = this.getSettings().isClickable();

        String format = Config.getString("mentions." + (isClicable ? "clickable_format" : "format"));
        for (Player player : collection) {
            message = message.replace(mentionString + player.getName(), format.replace("[mentioned]", player.getName()));
        }
        return isClicable ? message : ColorManager.color(message);
    }

    @Override
    public Collection<? extends Player> getMessageViewers(IChatUser sender) {
        Player player = sender.getPlayer();
        if (player == null) return null;

        Collection<Player> players = this.getSettings().getGlobalType().getList().apply(player);
        if (this.getSettings().getRange() > 0) {
            return players
                    .stream()
                    .filter(p -> p.getWorld().equals(player.getWorld()) && p.getLocation().distance(player.getLocation()) <= this.getSettings().getRange())
                    .collect(Collectors.toList());
        } else return players;
    }

    @Override
    public boolean isBlocked(IChatUser chatUser) {
        Player player = chatUser.getPlayer();
        if (player == null || !Tools.hasPermission(player, "satechat.use." + this.getSettings().getId())) {
            if (player != null) Config.sendMessage(player, "chatIsBlocked", "id-%-" + this.getSettings().getId());
            return true;
        }

        return false;
    }
}

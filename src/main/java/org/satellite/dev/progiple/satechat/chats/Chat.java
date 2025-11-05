package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.ComponentUtils;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.AfterSendChatEvent;
import org.satellite.dev.progiple.satechat.users.IChatUser;
import org.satellite.dev.progiple.satechat.utils.Tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        String endedMessage = Utils.setPlaceholders(player, this.getSettings().getFormat()).replace("[message]", message);
        endedMessage = ColorManager.color(endedMessage.replace("[sender]", player.getName()));

        String finalEndedMessage = endedMessage;
        viewers.forEach(p -> {
            if (isClickable) p.spigot().sendMessage(ComponentUtils.createClickableText(finalEndedMessage, ClickEvent.Action.SUGGEST_COMMAND));
            else p.sendMessage(finalEndedMessage);
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
        return Tools.hasMention(mentioned, message);
    }

    @Override
    public String mention(Collection<? extends Player> collection, String message) {
        return Tools.mention(collection, message, this.getSettings().isClickable());
    }

    @Override
    public Collection<? extends Player> getMessageViewers(IChatUser sender) {
        Player player = sender.getPlayer();
        if (player == null) return null;

        Collection<Player> players = this.getSettings().getGlobalType().getList().apply(player);
        String viewPermission = "satechat.view." + this.getSettings().getId();

        Stream<Player> stream = players.stream().filter(p -> Tools.hasPermission(p, viewPermission));
        if (this.getSettings().getRange() > 0) {
            Location playerLocation = player.getLocation();
            return stream
                    .filter(p -> p.getWorld().equals(player.getWorld()) && p.getLocation().distance(playerLocation) <= this.getSettings().getRange())
                    .collect(Collectors.toList());
        } else return stream.toList();
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

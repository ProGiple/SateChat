package org.satellite.dev.progiple.satechat.chats.state;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.API.util.service.managers.VanishManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.PrivateMessagingEvent;
import org.satellite.dev.progiple.satechat.users.ChatUserManager;
import org.satellite.dev.progiple.satechat.users.IChatUser;
import org.satellite.dev.progiple.satechat.utils.Tools;

import java.util.*;
import java.util.concurrent.TimeUnit;

@UtilityClass
public class PrivateManager {
    @Getter private final Map<IChatUser, IChatUser> replies = new HashMap<>();
    private CooldownPrevent<UUID> cooldown;

    public void initializeCooldown() {
        cooldown = new CooldownPrevent<>(Config.getInt("private_messages_cooldown_ticks") * 50L, TimeUnit.MILLISECONDS, 125);
    }

    public void sendPrivate(@NotNull IChatUser senderUser, @NotNull IChatUser recipientUser, @NotNull String message) {
        Player sender = Bukkit.getPlayer(senderUser.getUUID());
        if (sender == null || !sender.isOnline()) return;

        if (senderUser.getUUID().equals(recipientUser.getUUID())) {
            Config.sendMessage(sender, "private_messages.recipientIsYou");
            return;
        }

        Player recipient = VanishManager.exact(sender, recipientUser.getUUID());
        if (recipient == null || !recipient.isOnline()) {
            String name = recipient == null ? "" : recipient.getName();
            OfflinePlayer cachedPlayer = Bukkit.getOfflinePlayer(recipientUser.getUUID());
            if (name.isEmpty() && cachedPlayer != null) name = cachedPlayer.getName();

            Config.sendMessage(sender, "playerIsOffline", "player-%-" + name);
            return;
        }

        if (recipientUser.isIgnored(sender)) {
            Config.sendMessage(sender, "ignore.youAreIgnored", "player-%-" + recipient.getName());
            return;
        }

        if (cooldown.isCancelled(null, senderUser.getUUID())) {
            long value = cooldown.getRemaining(senderUser.getUUID());
            Config.sendMessage(sender, "chatCooldown", String.valueOf(LunaMath.round((double) value / 1000L, 1)));
            return;
        }

        if (Tools.allBlocks(sender, message, null)) return;
        message = Tools.allReplacements(sender, message, false);

        if (Config.useEvents()) {
            PrivateMessagingEvent privateMessagingEvent = new PrivateMessagingEvent(senderUser, recipientUser);
            privateMessagingEvent.setMessage(message);

            Bukkit.getPluginManager().callEvent(privateMessagingEvent);
            if (privateMessagingEvent.isCancelled()) return;

            message = privateMessagingEvent.getMessage();
        }

        String[] replacements = {"sender-%-" + sender.getName(), "recipient-%-" + recipient.getName(), "message-%-" + message};

        Config.sendMessage(sender, "private_messages.fromMe", replacements);
        Config.sendMessage(recipient, "private_messages.toMe", replacements);
        replies.put(recipientUser, senderUser);

        if (Tools.hasBypassPermission(sender, "spy") || Tools.hasBypassPermission(recipient, "spy")) return;
        for (Player spyPlayer : getSpyPlayers(senderUser, recipientUser)) {
            Config.sendMessage(spyPlayer, "spy.format",
                    "sender-%-" + sender.getName(),
                    "recipient-%-" + recipient.getName(),
                    "message-%-" + message);
        }
    }

    public boolean reply(IChatUser sender, String message) {
        IChatUser recipient = replies.get(sender);
        if (recipient == null) return false;

        sendPrivate(sender, recipient, message);
        return true;
    }

    public void sendPrivate(IChatUser sender, IChatUser recipient, List<String> messages) {
        sendPrivate(sender, recipient, String.join(" ", messages));
    }

    public boolean reply(IChatUser sender, List<String> messages) {
        return reply(sender, String.join(" ", messages));
    }

    public List<? extends Player> getSpyPlayers(IChatUser... ignored) {
        List<UUID> list = Arrays.stream(ignored).map(IChatUser::getUUID).toList();
        return Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> {
                    if (list.contains(p.getUniqueId())) return false;
                    return ChatUserManager.get(p.getUniqueId()).isEnabledSpy();
                }).toList();
    }
}

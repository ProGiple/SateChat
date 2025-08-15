package org.satellite.dev.progiple.satechat.chats.state;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.PrivateMessagingEvent;
import org.satellite.dev.progiple.satechat.users.ChatUserManager;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.*;

@UtilityClass
public class PrivateManager {
    @Getter private final Map<IChatUser, IChatUser> replies = new HashMap<>();

    public void sendPrivate(@NotNull IChatUser senderUser, @NotNull IChatUser recipientUser, @NotNull String message) {
        Player sender = Bukkit.getPlayer(senderUser.getUUID());
        if (sender == null || !sender.isOnline()) return;

        if (senderUser.getUUID().equals(recipientUser.getUUID())) {
            Config.sendMessage(sender, "private_messages.recipientIsYou");
            return;
        }

        OfflinePlayer recipient = Bukkit.getOfflinePlayer(recipientUser.getUUID());
        if (!recipient.isOnline()) {
            Config.sendMessage(sender, "playerIsOffline", "player-%-" + recipient.getName());
            return;
        }

        if (recipientUser.isIgnored(sender)) {
            Config.sendMessage(sender, "ignore.youAreIgnored", "player-%-" + recipient.getName());
            return;
        }

        if (Tools.adsBlocks(sender, message) || Tools.capsBlock(sender, message)) return;
        message = Tools.swearReplacement(sender, message);
        message = Tools.useColor(sender, message);
        message = Tools.replacementWords(sender, message);
        message = Tools.replacementCommands(message);

        PrivateMessagingEvent privateMessagingEvent = new PrivateMessagingEvent(senderUser, recipientUser);
        privateMessagingEvent.setMessage(message);

        Bukkit.getPluginManager().callEvent(privateMessagingEvent);
        if (privateMessagingEvent.isCancelled()) return;

        message = privateMessagingEvent.getMessage();
        String[] replacements = {"sender-%-" + sender.getName(), "recipient-%-" + recipient.getName(), "message-%-" + message};

        Config.sendMessage(sender, "private_messages.fromMe", replacements);
        Config.sendMessage((Player) recipient, "private_messages.toMe", replacements);
        replies.put(recipientUser, senderUser);

        if (Tools.hasBypassPermission(sender, "spy") || Tools.hasBypassPermission((Player) recipient, "spy")) return;
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

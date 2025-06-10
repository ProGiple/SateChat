package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.data.DataConfig;
import org.satellite.dev.progiple.satechat.configs.data.DataManager;
import org.satellite.dev.progiple.satechat.listeners.event.PrivateMessagingEvent;

import java.util.*;

@UtilityClass
public class PrivateManager {
    @Getter private final Map<UUID, UUID> replies = new HashMap<>();

    public void sendPrivate(@NotNull UUID senderUUID, @NotNull UUID recipientUUID, @NotNull String message) {
        Player sender = Bukkit.getPlayer(senderUUID);
        if (sender == null || !sender.isOnline()) return;

        if (senderUUID.equals(recipientUUID)) {
            Config.sendMessage(sender, "private_messages.recipientIsYou");
            return;
        }

        OfflinePlayer recipient = Bukkit.getOfflinePlayer(recipientUUID);
        if (!recipient.isOnline()) {
            Config.sendMessage(sender, "playerIsOffline", "player-%-" + recipient.getName());
            return;
        }

        if (DataManager.getConfig(recipientUUID).isIgnored(sender)) {
            Config.sendMessage(sender, "ignore.youAreIgnored", "player-%-" + recipient.getName());
            return;
        }

        if (Tools.adsBlocks(sender, message) || Tools.swearBlock(sender, message) || Tools.capsBlock(sender, message)) return;
        message = Tools.useColor(sender, Tools.replacementWords(sender, message));

        PrivateMessagingEvent privateMessagingEvent = new PrivateMessagingEvent(senderUUID, recipientUUID);
        privateMessagingEvent.setMessage(message);

        Bukkit.getPluginManager().callEvent(privateMessagingEvent);
        if (privateMessagingEvent.isCancelled()) return;

        message = privateMessagingEvent.getMessage();
        Config.sendMessage(sender, "private_messages.fromMe", "sender-%-" + sender.getName(),
                "recipient-%-" + recipient.getName(), "message-%-" + message);
        Config.sendMessage((Player) recipient, "private_messages.toMe", "sender-%-" + sender.getName(),
                "recipient-%-" + recipient.getName(), "message-%-" + message);
        replies.put(recipientUUID, senderUUID);

        if (Tools.hasBypassPermission(sender, "spy") || Tools.hasBypassPermission((Player) recipient, "spy")) return;
        @NotNull String finalMessage = message;
        getSpyPlayers(senderUUID, recipientUUID).forEach(p ->
                Config.sendMessage(p, "spy.format", "sender-%-" + sender.getName(),
                        "recipient-%-" + recipient.getName(), "message-%-" + finalMessage));
    }

    public boolean reply(UUID sender, String message) {
        UUID recipient = replies.get(sender);
        if (recipient == null) return false;

        sendPrivate(sender, recipient, message);
        return true;
    }

    public void sendPrivate(UUID sender, UUID recipient, List<String> messages) {
        sendPrivate(sender, recipient, String.join(" ", messages));
    }

    public boolean reply(UUID sender, List<String> messages) {
        return reply(sender, String.join(" ", messages));
    }

    public Collection<? extends Player> getSpyPlayers(UUID... ignored) {
        List<UUID> list = List.of(ignored);
        return Bukkit.getOnlinePlayers().stream().filter(p -> {
            if (list.contains(p.getUniqueId())) return false;

            DataConfig config = DataManager.getConfig(p.getUniqueId());
            if (config != null) return config.getBool("spy_mode");
            return false;
        }).toList();
    }
}

package org.satellite.dev.progiple.satechat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.chats.Chat;
import org.satellite.dev.progiple.satechat.chats.ChatManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.SendChatEvent;

import java.util.UUID;

public class SendChatHandler implements Listener {
    @EventHandler @SuppressWarnings("deprecation")
    public void onSend(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;

        e.setCancelled(true);
        Player player = e.getPlayer();

        String message = e.getMessage();
        if (message.isEmpty()) return;

        Chat chat = ChatManager.getChat(message.charAt(0));
        if (chat == null) {
            Config.sendMessage(player, "chatNotExists");
            return;
        }

        if (!Tools.hasPermission(player, "satechat.use." + chat.getId())) {
            Config.sendMessage(player, "chatIsBlocked", "id-%-" + chat.getId());
            return;
        }

        SendChatEvent sendChatEvent = new SendChatEvent(player, chat, message);
        Bukkit.getPluginManager().callEvent(sendChatEvent);
        if (!sendChatEvent.isCancelled()) {
            UUID uuid = player.getUniqueId();
            chat.sendMessage(uuid, sendChatEvent.getMessage());
        }
    }
}

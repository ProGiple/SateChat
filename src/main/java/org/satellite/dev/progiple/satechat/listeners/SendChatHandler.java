package org.satellite.dev.progiple.satechat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.satellite.dev.progiple.satechat.chats.RawChat;
import org.satellite.dev.progiple.satechat.chats.state.ChatManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.SendChatEvent;
import org.satellite.dev.progiple.satechat.users.ChatUserManager;
import org.satellite.dev.progiple.satechat.users.IChatUser;

public class SendChatHandler implements Listener {
    @EventHandler @SuppressWarnings("deprecation")
    public void onSend(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;

        e.setCancelled(true);
        Player player = e.getPlayer();

        String message = e.getMessage();
        if (message.isEmpty()) return;

        RawChat chat = ChatManager.getChat(message.charAt(0)).orElse(ChatManager.getChat());
        if (chat == null) {
            Config.sendMessage(player, "chatNotExists");
            return;
        }

        IChatUser chatUser = ChatUserManager.get(player.getUniqueId());
        if (chat.isBlocked(chatUser)) return;

        SendChatEvent sendChatEvent = new SendChatEvent(player, chatUser, chat, message);
        Bukkit.getPluginManager().callEvent(sendChatEvent);
        if (!sendChatEvent.isCancelled()) {
            chat.sendMessage(chatUser, sendChatEvent.getMessage());
        }
    }
}

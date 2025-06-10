package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ChatManager {
    @Getter private final Set<Chat> chats = new HashSet<>();

    public void register(Chat chat) {
        chats.add(chat);
    }

    public void unregister(Chat chat) {
        chats.remove(chat);
    }

    public void reload() {
        new HashSet<>(chats).forEach(c -> {if (c.getLunaPlugin().equals(SateChat.getINSTANCE())) chats.remove(c);});
        Config.getSection("chats")
                .getValues(false)
                .values()
                .forEach(v -> register(new Chat(SateChat.getINSTANCE(), (ConfigurationSection) v)));
    }

    public Chat getChat(String id) {
        return chats.stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public Chat getChat(char symbol) {
        return chats.stream().filter(c -> c.getSymbol() == symbol).findFirst().orElse(getChat());
    }

    public Chat getChat() {
        return chats
                .stream()
                .filter(c -> c.getSymbol() == ' ' || String.valueOf(c.getSymbol()).isEmpty())
                .findFirst()
                .orElse(null);
    }
}

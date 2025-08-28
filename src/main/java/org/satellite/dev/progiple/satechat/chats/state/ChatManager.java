package org.satellite.dev.progiple.satechat.chats.state;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.chats.Chat;
import org.satellite.dev.progiple.satechat.chats.ChatSettings;
import org.satellite.dev.progiple.satechat.chats.RawChat;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@UtilityClass
public class ChatManager {
    @Getter private final Set<RawChat> chats = new HashSet<>();

    public void register(RawChat chat) {
        chats.add(chat);
    }

    public void unregister(RawChat chat) {
        chats.remove(chat);
    }

    public void reload() {
        new HashSet<>(chats).forEach(c -> {
            if (c.getPlugin().equals(SateChat.getINSTANCE())) chats.remove(c);
        });
        Config.getSection("chats")
                .getValues(false)
                .values()
                .forEach(v -> register(
                        new Chat(SateChat.getINSTANCE(), new ChatSettings((ConfigurationSection) v))));
    }

    public @NotNull Optional<RawChat> getChat(String id) {
        return Utils.find(chats, c -> c.getSettings().getId().equalsIgnoreCase(id));
    }

    public @NotNull Optional<RawChat> getChat(char symbol) {
        return Utils.find(chats, c -> c.getSettings().getSymbol() == symbol);
    }

    public Stream<RawChat> getChats(LunaPlugin lunaPlugin) {
        return chats.stream().filter(c -> c.getPlugin().equals(lunaPlugin));
    }

    public @Nullable RawChat getChat() {
        return chats
                .stream()
                .filter(c -> c.getSettings().getSymbol() == ' ' || String.valueOf(c.getSettings().getSymbol()).isEmpty())
                .findFirst()
                .orElse(null);
    }
}

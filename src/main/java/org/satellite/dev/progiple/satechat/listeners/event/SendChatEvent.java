package org.satellite.dev.progiple.satechat.listeners.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.satechat.chats.RawChat;
import org.satellite.dev.progiple.satechat.users.IChatUser;

@Getter
public class SendChatEvent extends PlayerEvent implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final RawChat chat;
    private final IChatUser playerChatUser;
    @Setter private String message;
    @Setter private boolean cancelled = false;
    public SendChatEvent(@NotNull Player who, @NotNull IChatUser playerChatUser, @NotNull RawChat chat, @NotNull String message) {
        super(who, true);
        this.chat = chat;
        this.message = message;
        this.playerChatUser = playerChatUser;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

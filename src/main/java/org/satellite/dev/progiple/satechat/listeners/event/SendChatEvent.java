package org.satellite.dev.progiple.satechat.listeners.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.satechat.chats.RawChat;

@Getter
public class SendChatEvent extends PlayerEvent implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final RawChat chat;
    @Setter private String message;
    @Setter private boolean cancelled = false;
    public SendChatEvent(@NotNull Player who, @NonNull RawChat chat, @NonNull String message) {
        super(who, true);
        this.chat = chat;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

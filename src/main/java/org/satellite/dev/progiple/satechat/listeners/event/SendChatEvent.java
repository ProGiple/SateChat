package org.satellite.dev.progiple.satechat.listeners.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.satechat.chats.Chat;

public class SendChatEvent extends PlayerEvent implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();

    @Getter private final Chat chat;
    @Getter @Setter private String message;
    private boolean cancel = false;
    public SendChatEvent(@NotNull Player who, @NonNull Chat chat, @NonNull String message) {
        super(who, true);
        this.chat = chat;
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}

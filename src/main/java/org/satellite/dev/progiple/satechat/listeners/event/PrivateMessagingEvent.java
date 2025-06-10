package org.satellite.dev.progiple.satechat.listeners.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter @RequiredArgsConstructor
public class PrivateMessagingEvent extends Event implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();
    private final UUID sender;
    private final UUID recipient;

    @Setter private String message;
    private boolean cancel = false;

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

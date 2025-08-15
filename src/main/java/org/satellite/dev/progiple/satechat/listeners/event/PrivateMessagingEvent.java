package org.satellite.dev.progiple.satechat.listeners.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.satechat.users.IChatUser;

@Getter @Setter @RequiredArgsConstructor
public class PrivateMessagingEvent extends Event implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();
    private final IChatUser sender;
    private final IChatUser recipient;

    private String message;
    private boolean cancelled = false;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

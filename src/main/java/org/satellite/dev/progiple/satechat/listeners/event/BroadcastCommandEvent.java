package org.satellite.dev.progiple.satechat.listeners.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter @Setter @RequiredArgsConstructor
public class BroadcastCommandEvent extends Event implements Cancellable {
    @Getter private static final HandlerList handlerList = new HandlerList();
    private final CommandSender sender;

    private String message;
    private boolean cancelled = false;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

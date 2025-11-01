package org.satellite.dev.progiple.satechat.listeners.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.satechat.chats.RawChat;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.Collection;

@Getter
public class AfterSendChatEvent extends PlayerEvent {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final RawChat chat;
    private final IChatUser playerChatUser;
    private final String message;
    private final Collection<? extends Player> viewers;
    public AfterSendChatEvent(@NotNull Player who,
                              @NotNull IChatUser playerChatUser,
                              @NotNull RawChat chat,
                              @NotNull String message,
                              Collection<? extends Player> viewers) {
        super(who, true);
        this.chat = chat;
        this.message = message;
        this.playerChatUser = playerChatUser;
        this.viewers = viewers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

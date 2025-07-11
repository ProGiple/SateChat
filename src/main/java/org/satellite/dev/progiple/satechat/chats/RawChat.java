package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.LunaPlugin;

import java.util.Collection;
import java.util.UUID;

@Getter
public abstract class RawChat {
    private final LunaPlugin plugin;
    private final CooldownPrevent<UUID> cooldownPrevent;

    @Setter private ChatSettings settings;
    public RawChat(LunaPlugin plugin, ChatSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        this.cooldownPrevent = new CooldownPrevent<>(settings.getCooldownTicks() * 50);
    }

    public abstract boolean sendMessage(UUID sender, final String rawMessage);

    public abstract boolean hasMention(CommandSender mentioned, String message);

    public abstract String mention(Collection<? extends Player> viewers, String message);

    public abstract Collection<? extends Player> getMessageViewers(UUID sender);
}

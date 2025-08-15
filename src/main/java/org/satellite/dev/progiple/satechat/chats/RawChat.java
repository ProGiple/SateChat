package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.Collection;

@Getter
public abstract class RawChat {
    private final LunaPlugin plugin;
    private final CooldownPrevent<IChatUser> cooldownPrevent;

    @Setter private ChatSettings settings;
    public RawChat(LunaPlugin plugin, ChatSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        this.cooldownPrevent = new CooldownPrevent<>(settings.getCooldownTicks() * 50);
    }

    public abstract boolean sendMessage(IChatUser sender, final String rawMessage);

    public abstract boolean hasMention(CommandSender mentioned, String message);

    public abstract String mention(Collection<? extends Player> viewers, String message);

    public abstract Collection<? extends Player> getMessageViewers(IChatUser sender);
}

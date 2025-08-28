package org.satellite.dev.progiple.satechat.chats;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.users.IChatUser;

import java.util.Collection;

@Getter @Setter
public abstract class RawChat implements Cloneable {
    private final LunaPlugin plugin;
    private CooldownPrevent<IChatUser> cooldownPrevent;

    private ChatSettings settings;
    public RawChat(LunaPlugin plugin, ChatSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        this.cooldownPrevent = new CooldownPrevent<>(Math.max(settings.getCooldownTicks() * 50, 0));
    }

    public abstract boolean sendMessage(IChatUser sender, final String rawMessage);

    public abstract boolean hasMention(CommandSender mentioned, String message);

    public abstract String mention(Collection<? extends Player> viewers, String message);

    public abstract Collection<? extends Player> getMessageViewers(IChatUser sender);

    public abstract boolean isBlocked(IChatUser chatUser);

    @Override
    public RawChat clone() {
        try {
            RawChat clone = (RawChat) super.clone();
            clone.cooldownPrevent = this.cooldownPrevent.clone();
            clone.settings = this.settings.duplicate();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

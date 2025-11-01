package org.satellite.dev.progiple.satechat.configs;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.satellite.dev.progiple.satechat.SateChat;
import org.satellite.dev.progiple.satechat.chats.state.PrivateManager;
import org.satellite.dev.progiple.satechat.commands.broadcast.BroadCastCommand;

@UtilityClass
public class Config {
    private final IConfig config;
    @Getter @Accessors(fluent = true) private boolean useEvents;
    static {
        config = new IConfig(SateChat.getINSTANCE());
        useEvents = config.getBoolean("disable_events");
    }

    public void reload() {
        config.reload(SateChat.getINSTANCE());
        useEvents = config.getBoolean("disable_events");

        PrivateManager.initializeCooldown();
        BroadCastCommand.setCdPrevent();
        SateChat.getINSTANCE().resetAutoMessager();
    }

    public @NotNull ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public void sendMessage(CommandSender sender, String id, String... rpl) {
        config.sendMessage(sender, id, rpl);
    }

    public void sendMessage(String messId, CommandSender sender, String id, String... rpl) {
        config.sendMessage(messId, sender, id, rpl);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }
}

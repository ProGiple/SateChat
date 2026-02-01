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

import java.util.List;

@UtilityClass @Accessors(fluent = true)
public class Config {
    private final IConfig config;
    @Getter private boolean useEvents;
    @Getter private boolean optimizedStorage;
    static {
        config = new IConfig(SateChat.getINSTANCE());
        loadOptimized();
    }

    public void reload() {
        config.reload(SateChat.getINSTANCE());
        loadOptimized();

        PrivateManager.initializeCooldown();
        BroadCastCommand.setCdPrevent();
        SateChat.getINSTANCE().resetAutoMessager();
    }

    private void loadOptimized() {
        ConfigurationSection section = getSection("optimize");
        useEvents = !section.getBoolean("disable_events");
        optimizedStorage = section.getBoolean("optimizedStorage");
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

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public List<String> getList(String path) {
        return config.getStringList(path);
    }
}

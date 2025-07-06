package org.satellite.dev.progiple.satechat;

import lombok.Getter;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.chats.ChatManager;
import org.satellite.dev.progiple.satechat.commands.broadcast.BroadCastCommand;
import org.satellite.dev.progiple.satechat.listeners.LeaveJoinHandler;
import org.satellite.dev.progiple.satechat.listeners.SendChatHandler;

public final class SateChat extends LunaPlugin {
    @Getter private static SateChat INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        super.onEnable();
        saveDefaultConfig();

        ChatManager.reload();
        BroadCastCommand.setCdPrevent();
        this.processCommands();

        LunaExecutor.initialize(this);
        this.registerListeners(new LeaveJoinHandler(), new SendChatHandler());

        AutoMessager.resetSettings();
        new AutoMessager().runTaskTimer(SateChat.getINSTANCE(), 20L, 20L);
    }
}

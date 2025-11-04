package org.satellite.dev.progiple.satechat;

import lombok.Getter;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.chats.state.ChatManager;
import org.satellite.dev.progiple.satechat.chats.state.PrivateManager;
import org.satellite.dev.progiple.satechat.commands.broadcast.BroadCastCommand;
import org.satellite.dev.progiple.satechat.configs.ads.AdsManager;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.replacements.ReplacementsManager;
import org.satellite.dev.progiple.satechat.configs.swears.SwearsManager;
import org.satellite.dev.progiple.satechat.listeners.LeaveJoinHandler;
import org.satellite.dev.progiple.satechat.listeners.SendChatHandler;
import org.satellite.dev.progiple.satechat.utils.AutoMessager;

public final class SateChat extends LunaPlugin {
    @Getter private static SateChat INSTANCE;
    private AutoMessager autoMessager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        super.onEnable();
        if (!INSTANCE.getDataFolder().exists()) {
            this.loadFiles("blocks/ads.yml", "blocks/swears.yml", "blocks/replacements.yml");
        }

        saveDefaultConfig();
        PrivateManager.initializeCooldown();
        ChatManager.reload();

        AdsManager.initialize();
        SwearsManager.initialize();
        ReplacementsManager.initialize();

        BroadCastCommand.setCdPrevent();

        this.processCommands("#.commands");
        this.registerListeners(new LeaveJoinHandler(), new SendChatHandler());

        this.resetAutoMessager();
    }

    @Override
    public void onDisable() {
        if (this.autoMessager != null) this.autoMessager.cancel();
        super.onDisable();
    }

    public void resetAutoMessager() {
        if (this.autoMessager != null) this.autoMessager.cancel();

        long time = Config.getInt("auto_messages_time") * 20L;
        if (time < 20L) return;

        this.autoMessager = new AutoMessager();
        this.autoMessager.runTaskTimerAsynchronously(this, time, time);
    }
}

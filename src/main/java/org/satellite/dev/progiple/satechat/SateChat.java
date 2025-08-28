package org.satellite.dev.progiple.satechat;

import lombok.Getter;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.satechat.chats.state.ChatManager;
import org.satellite.dev.progiple.satechat.commands.broadcast.BroadCastCommand;
import org.satellite.dev.progiple.satechat.configs.AdsConfig;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.configs.ReplacementsConfig;
import org.satellite.dev.progiple.satechat.configs.SwearsConfig;
import org.satellite.dev.progiple.satechat.listeners.LeaveJoinHandler;
import org.satellite.dev.progiple.satechat.listeners.SendChatHandler;

import java.io.File;

public final class SateChat extends LunaPlugin {
    @Getter private static SateChat INSTANCE;
    private AutoMessager autoMessager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        super.onEnable();
        saveDefaultConfig();

        File mainDir = new File(INSTANCE.getDataFolder(), "data/");
        if (!mainDir.exists()) {
            this.loadFiles("blocks/ads.yml", "blocks/swears.yml", "blocks/replacements.yml");
        }

        ChatManager.reload();
        AdsConfig.setAdsConfig(new AdsConfig(Config.getString("advertisement_block.file")));
        SwearsConfig.setSwearsConfig(new SwearsConfig(Config.getString("swear_block.file")));
        ReplacementsConfig.setReplacementsConfig(new ReplacementsConfig(Config.getString("replacement_words.file")));

        BroadCastCommand.setCdPrevent();

        this.processCommands("#.commands");
        this.registerListeners(new LeaveJoinHandler(), new SendChatHandler());

        AutoMessager.resetSettings();
        this.autoMessager = new AutoMessager();
        this.autoMessager.runTaskTimerAsynchronously(SateChat.getINSTANCE(), 20L, 20L);
    }

    @Override
    public void onDisable() {
        if (this.autoMessager != null) this.autoMessager.cancel();
        super.onDisable();
    }
}

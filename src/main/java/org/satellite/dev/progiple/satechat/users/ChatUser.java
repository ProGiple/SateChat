package org.satellite.dev.progiple.satechat.users;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.satellite.dev.progiple.satechat.configs.DataConfig;

import java.util.List;
import java.util.UUID;

@Getter
public class ChatUser implements IChatUser {
    private final DataConfig dataConfig;
    private boolean enabledSpy;
    private boolean enabledIgnoreAll;
    public ChatUser(UUID uuid) {
        this.dataConfig = new DataConfig(uuid);
        this.enabledSpy = this.dataConfig.getBool("spy_mode");
        this.enabledIgnoreAll = this.dataConfig.getBool("ignore_all");
    }

    @Override
    public UUID getUUID() {
        return this.dataConfig.getUuid();
    }

    @Override
    public List<String> getIgnoreList() {
        return this.dataConfig.getIgnored();
    }

    @Override
    public boolean switchSpy() {
        this.enabledSpy = this.dataConfig.switchSpyMode();
        return this.enabledSpy;
    }

    @Override
    public boolean switchIgnoreAll() {
        this.enabledIgnoreAll = this.dataConfig.switchIgnore();
        return this.enabledIgnoreAll;
    }

    @Override
    public boolean switchIgnore(Player player) {
        return this.dataConfig.switchIgnore(player.getName());
    }

    @Override
    public boolean switchIgnore(String playerNick) {
        return this.dataConfig.switchIgnore(playerNick);
    }

    @Override
    public boolean isIgnored(Player player) {
        return this.isIgnored(player.getName());
    }

    @Override
    public boolean isIgnored(String playerNick) {
        return this.enabledIgnoreAll || this.getIgnoreList().contains(playerNick);
    }
}

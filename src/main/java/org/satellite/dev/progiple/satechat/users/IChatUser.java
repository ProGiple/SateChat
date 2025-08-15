package org.satellite.dev.progiple.satechat.users;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface IChatUser {
    UUID getUUID();
    Collection<String> getIgnoreList();
    boolean switchSpy();
    boolean switchIgnoreAll();
    boolean switchIgnore(Player player);
    boolean switchIgnore(String playerNick);
    boolean isEnabledSpy();
    boolean isEnabledIgnoreAll();
    boolean isIgnored(Player player);
    boolean isIgnored(String playerNick);
}

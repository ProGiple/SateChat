package org.satellite.dev.progiple.satechat.users;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface IChatUser {
    @NotNull UUID getUUID();
    @NotNull Collection<String> getIgnoreList();
    @Nullable Player getPlayer();
    boolean switchSpy();
    boolean switchIgnoreAll();
    boolean switchIgnore(Player player);
    boolean switchIgnore(String playerNick);
    boolean isEnabledSpy();
    boolean isEnabledIgnoreAll();
    boolean isIgnored(Player player);
    boolean isIgnored(String playerNick);
}

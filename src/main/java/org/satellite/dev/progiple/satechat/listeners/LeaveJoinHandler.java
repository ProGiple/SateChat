package org.satellite.dev.progiple.satechat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.users.ChatUserManager;

import java.util.UUID;

public class LeaveJoinHandler implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        this.broadcastNotifier(e.getPlayer(), "onJoin");
        e.joinMessage(null);

        UUID uuid = e.getPlayer().getUniqueId();
        if (!ChatUserManager.contains(uuid)) ChatUserManager.create(uuid);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.quitMessage(null);
        this.broadcastNotifier(e.getPlayer(), "onQuit");

        UUID uuid = e.getPlayer().getUniqueId();
        if (ChatUserManager.contains(uuid)) ChatUserManager.unregister(ChatUserManager.get(uuid));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        e.deathMessage(null);

        Player killer = player.getKiller();
        if (killer != null) {
            Config.sendMessage(player, "vanilla.onDeathFromPlayer",
                    "player-%-" + player.getName(), "killer-%-" + killer.getName());
        } else this.broadcastNotifier(player, "onDeath");
    }

    private void broadcastNotifier(Player player, String path) {
        Config.sendMessage(player, "vanilla." + path, "player-%-" + player.getName());
    }
}

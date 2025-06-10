package org.satellite.dev.progiple.satechat;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.List;

public class AutoMessager extends BukkitRunnable {
    @Getter private static ConfigurationSection messages;
    @Getter private static int messageTime;

    private int seconds = 0;

    @Override
    public void run() {
        if (messages == null || messageTime <= 0) return;

        if (this.seconds < messageTime) {
            this.seconds++;
            return;
        }

        this.seconds = 0;
        List<String> keys = messages.getKeys(false).stream().toList();

        String messageId = keys.get(LunaMath.getRandomInt(0, keys.size()));
        Bukkit.getScheduler().runTask(SateChat.getINSTANCE(), () -> Utils.playersAction(p -> {
            if (!Tools.hasBypassPermission(p, "automessages")) Config.sendMessage(p, "auto_messages." + messageId);
        }));
    }

    public static void resetSettings() {
        messages = Config.getSection("messages.auto_messages");
        messageTime = Config.getInt("auto_messages_time");
    }
}

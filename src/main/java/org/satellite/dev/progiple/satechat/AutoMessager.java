package org.satellite.dev.progiple.satechat;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.ArrayList;
import java.util.List;

public class AutoMessager extends BukkitRunnable {
    @Getter private static ConfigurationSection messages;
    @Getter private static int messageTime;

    private int seconds = 0;
    private String beforeId;

    @Override
    public void run() {
        if (messages == null || messageTime <= 0) return;

        if (this.seconds < messageTime) {
            this.seconds++;
            return;
        }

        this.seconds = 0;
        List<String> keys = new ArrayList<>(messages.getKeys(false));
        if (keys.isEmpty()) return;

        String messageId;
        if (keys.size() == 1) messageId = keys.get(0);
        else {
            keys.remove(this.beforeId);
            messageId = keys.get(LunaMath.getRandomInt(0, keys.size()));
        }

        this.beforeId = messageId;
        Utils.playersAction(p -> {
            if (!Tools.hasBypassPermission(p, "automessages")) Config.sendMessage("auto_messages", p, messageId);
        });
    }

    public static void resetSettings() {
        messages = Config.getSection("auto_messages");
        messageTime = Config.getInt("auto_messages_time");
    }
}

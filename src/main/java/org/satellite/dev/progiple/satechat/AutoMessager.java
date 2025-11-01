package org.satellite.dev.progiple.satechat;

import com.google.common.collect.Lists;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.*;

public class AutoMessager extends BukkitRunnable {
    private final Set<String> beforeIds = new HashSet<>();
    private final Map<String, List<String>> messages = new HashMap<>();
    public AutoMessager() {
        ConfigurationSection section = Config.getSection("auto_messages");
        for (String key : section.getKeys(false)) {
            List<String> list = Lists.newArrayList(section.getString(key));
            if (list.isEmpty()) {
                list.add(section.getString(key));
            }

            this.messages.put(key, list);
        }
    }

    @Override
    public void run() {
        List<String> keys = new ArrayList<>(this.messages.keySet());
        if (keys.isEmpty()) return;

        String messageId;
        if (keys.size() == 1) messageId = keys.get(0);
        else {
            if (keys.size() <= this.beforeIds.size()) this.beforeIds.clear();
            else keys.removeAll(this.beforeIds);
            messageId = keys.get(LunaMath.getRandomInt(0, keys.size()));
        }

        this.beforeIds.add(messageId);
        Utils.playersAction(p -> {
            if (!Tools.hasBypassPermission(p, "automessages")) AnnounceUtils.sendMessage(p, this.messages.get(messageId));
        });
    }
}

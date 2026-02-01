package org.satellite.dev.progiple.satechat.utils;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.API.util.utilities.lists.LunaList;
import org.novasparkle.lunaspring.API.util.utilities.tasks.LunaTask;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoMessager extends LunaTask {
    private final LunaList<String> keys = new LunaList<>();
    private final Map<String, List<String>> messages = new HashMap<>();
    public AutoMessager(long time) {
        super(time * 50L);
        ConfigurationSection section = Config.getSection("auto_messages");
        for (String key : section.getKeys(false)) {
            List<String> list = Lists.newArrayList(section.getStringList(key));
            if (list.isEmpty()) {
                list.add(section.getString(key));
            }

            this.messages.put(key, list);
        }
    }

    @Override @SneakyThrows
    @SuppressWarnings("all")
    public void start() {
        while (true) {
            Thread.sleep(this.getTicks());
            if (this.messages.isEmpty()) continue;

            if (this.keys.isEmpty()) {
                this.keys.addAll(this.messages.keySet());
            }

            String messageId = this.keys.randomElement();
            this.keys.remove(messageId);

            Utils.playersAction(p -> {
                if (!Tools.hasBypassPermission(p, "automessages")) AnnounceUtils.sendMessage(p, this.messages.get(messageId));
            });
        }
    }
}

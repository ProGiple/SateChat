package org.satellite.dev.progiple.satechat.configs.data;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class DataManager {
    @Getter private final Set<DataConfig> configs = new HashSet<>();

    public void register(DataConfig dataConfig) {
        configs.add(dataConfig);
    }

    public void unregister(DataConfig dataConfig) {
        configs.remove(dataConfig);
    }

    public DataConfig getConfig(UUID uuid) {
        return configs.stream().filter(c -> c.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}

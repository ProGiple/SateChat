package org.satellite.dev.progiple.satechat.chats;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.util.utilities.Utils;

import java.util.Collection;
import java.util.function.Function;

@AllArgsConstructor @Builder @Getter @Setter
public class ChatSettings {
    private @NotNull String id;
    private @NotNull String format;
    private char symbol;
    private int range;
    private boolean clickable;
    private final int cooldownTicks;
    private GlobalType globalType;
    private String logger;
    public ChatSettings(ConfigurationSection section) {
        this.id = section.getName();
        this.range = section.getKeys(false).contains("range") ? section.getInt("range") : -1;

        String format = section.getString("format");
        this.format = format != null && !format.isEmpty() ? format : "%player_name% -> {message}";

        String logger = section.getString("logger");
        this.logger = logger != null && !logger.isEmpty() ? logger : this.format;

        String symbol = section.getString("symbol");
        this.symbol = symbol != null && !symbol.isEmpty() ? symbol.charAt(0) : ' ';

        this.cooldownTicks = section.getKeys(false).contains("cooldownTicks") ? section.getInt("cooldownTicks") : 0;
        this.clickable = section.getBoolean("clickable");
        this.globalType = Utils.getEnumValue(GlobalType.class, section.getString("range_type"), GlobalType.ALL_WORLDS);
    }

    public ChatSettings duplicate() {
        return ChatSettings.builder()
                .id(this.id)
                .format(this.format)
                .symbol(this.symbol)
                .cooldownTicks(this.cooldownTicks)
                .range(this.range)
                .clickable(this.clickable)
                .globalType(this.globalType)
                .logger(this.logger)
                .build();
    }

    @RequiredArgsConstructor @Getter
    public enum GlobalType {
        ALL_WORLDS(p -> Bukkit.getWorlds().stream().flatMap(w -> w.getPlayers().stream()).toList()),
        IN_TARGET_WORLD(p -> p.getWorld().getPlayers());

        private final Function<Player, Collection<Player>> list;
    }
}

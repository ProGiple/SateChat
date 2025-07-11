package org.satellite.dev.progiple.satechat.chats;

import lombok.*;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatSettings {
    private @NotNull String id;
    private @NotNull String format;
    private char symbol;
    private int range;
    private final int cooldownTicks;
    public ChatSettings(ConfigurationSection section) {
        this.id = section.getName();
        this.range = section.getKeys(false).contains("range") ? section.getInt("range") : -1;

        String format = section.getString("format");
        this.format = format != null && !format.isEmpty() ? format : "%player_name% -> {message}";

        String symbol = section.getString("symbol");
        this.symbol = symbol != null && !symbol.isEmpty() ? symbol.charAt(0) : ' ';

        this.cooldownTicks = section.getKeys(false).contains("cooldownTicks") ? section.getInt("cooldownTicks") : 0;
    }

    public ChatSettings duplicate() {
        return ChatSettings.builder()
                .id(this.id)
                .format(this.format)
                .symbol(this.symbol)
                .cooldownTicks(this.cooldownTicks)
                .range(this.range)
                .build();
    }
}

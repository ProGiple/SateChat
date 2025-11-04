package org.satellite.dev.progiple.satechat.configs;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface IFileConfig {
    boolean isOptimized();
    boolean isEnabled();
    List<String> getActionCommands();
    String process(CommandSender sender, String message);
}

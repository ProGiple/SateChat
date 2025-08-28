package org.satellite.dev.progiple.satechat.commands.broadcast;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.commands.annotations.LunaCommand;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.BroadcastCommandEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

@LunaCommand(value = "broadcast")
public class BroadCastCommand implements TabExecutor {
    private static CooldownPrevent<CommandSender> cd = new CooldownPrevent<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!Tools.hasPermission(commandSender, "satechat.broadcast")) {
            Config.sendMessage(commandSender, "noPermission");
            return true;
        }

        if (strings.length == 0) {
            Config.sendMessage(commandSender, "usage.broadcast");
            return true;
        }

        if ((!Tools.hasBypassPermission(commandSender, "cooldown") && cd.isCancelled(null, commandSender))) {
            long value = cd.getCache().get(commandSender, k -> 0L);
            Config.sendMessage(commandSender, "chatCooldown", String.valueOf((value - System.currentTimeMillis()) / 1000L));
            return true;
        }

        String starterMessage = String.join(" ", List.of(strings).subList(0, strings.length));
        if (Tools.adsBlocks(commandSender, starterMessage) || Tools.capsBlock(commandSender, starterMessage)) return true;

        BroadcastCommandEvent event = new BroadcastCommandEvent(commandSender);
        event.setMessage(starterMessage);

        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getMessage().isEmpty()) return true;

        String message = event.getMessage();
        message = Tools.swearReplacement(commandSender, message);
        message = Tools.useColor(commandSender, message);
        message = Tools.replacementWords(commandSender, message);
        BroadCastCommand.send(commandSender, Tools.replacementCommands(message, false));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? List.of("<message>") : List.of();
    }

    public static void send(CommandSender sender, String message) {
        Config.sendMessage(sender, "broadcast", "message-%-" + message, "sender-%-" + sender.getName());
    }

    public static void setCdPrevent() {
        cd = new CooldownPrevent<>(Math.max(Config.getInt("broadcast_cooldown"), 0), TimeUnit.SECONDS, 125);
    }
}

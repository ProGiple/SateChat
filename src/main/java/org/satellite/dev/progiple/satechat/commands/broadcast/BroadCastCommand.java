package org.satellite.dev.progiple.satechat.commands.broadcast;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.commands.annotations.LunaCommand;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.API.util.utilities.AnnounceUtils;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.satechat.utils.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;
import org.satellite.dev.progiple.satechat.listeners.event.BroadcastCommandEvent;

import java.util.Collection;
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
            long value = cd.getRemaining(commandSender);
            Config.sendMessage(commandSender, "chatCooldown", String.valueOf(LunaMath.round((double) value / 1000L, 1)));
            return true;
        }

        String starterMessage = String.join(" ", List.of(strings).subList(0, strings.length));
        if (Tools.allBlocks(commandSender, starterMessage, null)) return true;

        String message = Tools.useColor(commandSender, starterMessage);
        if (Config.useEvents()) {
            BroadcastCommandEvent event = new BroadcastCommandEvent(commandSender);
            event.setMessage(message);

            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled() || event.getMessage().isEmpty()) return true;
            message = event.getMessage();
        }

        message = Tools.allReplacements(commandSender, message, false);

        Collection<? extends Player> viewers = Bukkit.getOnlinePlayers();
        message = Tools.mention(viewers, message, false);

        String strSound = Config.getString("mentions.sound");
        for (Player viewer : viewers) {
            if (Tools.hasMention(viewer, message)) AnnounceUtils.sound(viewer, strSound);
        }
        BroadCastCommand.send(commandSender, Tools.replacementCommands(message, false));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? List.of("<message>") : List.of();
    }

    public static void send(CommandSender sender, String message) {
        String senderName = sender instanceof ConsoleCommandSender ? Config.getString("messages.console") : sender.getName();
        Config.sendMessage(sender, "broadcast", "message-%-" + message, "sender-%-" + senderName);
    }

    public static void setCdPrevent() {
        int time = Math.max(Config.getInt("broadcast_cooldown"), 0);
        cd = new CooldownPrevent<>(time, TimeUnit.SECONDS, 125);
    }
}

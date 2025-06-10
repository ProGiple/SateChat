package org.satellite.dev.progiple.satechat.commands.broadcast;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.commands.annotations.LunaCommand;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.satellite.dev.progiple.satechat.Tools;
import org.satellite.dev.progiple.satechat.configs.Config;

import java.util.List;

@LunaCommand(value = "broadcast")
public class BroadCastCommand implements TabExecutor {
    private static final CooldownPrevent<CommandSender> cd = new CooldownPrevent<>();

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
            Config.sendMessage(commandSender, "chatCooldown", String.valueOf(
                    (cd.getCooldownMap().get(commandSender) - System.currentTimeMillis()) / 1000L));
            return true;
        }

        String starterMessage = String.join(" ", List.of(strings).subList(0, strings.length));
        if (Tools.adsBlocks(commandSender, starterMessage)
                || Tools.swearBlock(commandSender, starterMessage)
                || Tools.capsBlock(commandSender, starterMessage)) return true;

        send(commandSender, Tools.replacementWords(commandSender, starterMessage));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return strings.length == 1 ? List.of("<message>") : List.of();
    }

    public static void send(CommandSender sender, String message) {
        Config.sendMessage(sender, "broadcast", "message-%-" + message);
    }

    public static void setCdPrevent() {
        cd.setCooldownMS(Config.getInt("broadcast_cooldown") * 1000);
    }
}

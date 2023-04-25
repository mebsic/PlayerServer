package com.playground.spigot.commands;

import com.playground.spigot.PlayerServer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class InviteCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.getUniqueId().equals(PlayerServer.getServerName())) {
                if (args.length == 1) {
                    UUID uuid = null;

                    try {
                        uuid = PlayerServer.getInstance().findPlayer(args[0]);
                    } catch (Exception ignored) {}

                    if (uuid != null && !PlayerServer.getInstance().getSqlInviteManager().exists(uuid, PlayerServer.getServerName())) {
                        PlayerServer.getInstance().getSqlInviteManager().addPlayer(uuid, PlayerServer.getServerName());
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1.0f, 1.0f);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + args[0] + " has been invited to the SMP!"));
                    } else if (uuid != null && PlayerServer.getInstance().getSqlInviteManager().exists(uuid, PlayerServer.getServerName())) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + args[0] + " is already invited to the SMP! Remove them using &b/remove " + args[0]));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + args[0] + " was not found! Please try again."));
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid command! Correct usage: &b/invite <player>"));
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to run that command!"));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed!"));
        }

        return false;
    }
}

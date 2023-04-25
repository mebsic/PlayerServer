package com.playground.spigot.commands;

import com.google.common.base.Charsets;
import com.playground.spigot.PlayerServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.UUID;

public class RemoveCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.getUniqueId().equals(PlayerServer.getServerName())) {
                if (args.length == 1) {
                    UUID uuid = null;

                    try {
                        uuid = PlayerServer.getInstance().findPlayer(args[0]);
                    } catch (Exception ignored) {
                    }

                    if (uuid != null && PlayerServer.getInstance().getSqlInviteManager().exists(uuid, PlayerServer.getServerName())) {
                        removePlayer(uuid, p);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + args[0] + " was removed from the SMP!"));
                    } else if (uuid != null && !PlayerServer.getInstance().getSqlInviteManager().exists(uuid, PlayerServer.getServerName())) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + args[0] + " is not invited to the SMP! Invite them using &b/invite " + args[0]));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + args[0] + " was not found! Please try again."));
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid command! Correct usage: &b/remove <player>"));
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to run that command!"));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed!"));
        }

        return false;
    }

    private void removePlayer(UUID uuid, Player owner) {
        String playerRemovedMessage = "&cYou have been removed from " + owner.getName() + "'s SMP!";
        PlayerServer.getInstance().getSqlInviteManager().removePlayer(uuid, PlayerServer.getServerName());
        Player removedPlayer = Bukkit.getPlayer(uuid);

        if (removedPlayer != null) {
            removedPlayer.sendPluginMessage(PlayerServer.getInstance(), "bungeecord:uninvite_player", playerRemovedMessage.getBytes(Charsets.UTF_8));
        }
    }
}

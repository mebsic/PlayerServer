package com.playground.spigot.commands;

import com.playground.spigot.PlayerServer;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CreditsCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.getUniqueId().equals(PlayerServer.getServerName())) {
                if (args.length == 1) {
                    Player player = Bukkit.getPlayerExact(args[0]);

                    if (player != null && player.isOnline()) {
                        PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.e, 1);
                        ((CraftPlayer) player).getHandle().c.a(packet);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aNow playing end credits for " + args[0] + "!"));
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + args[0] + " was not found! Please try again."));
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid command! Correct usage: &b/credits <player>"));
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

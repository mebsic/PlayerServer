package com.playground.spigot.commands;

import com.playground.spigot.ServerMonitor;
import com.playground.spigot.listeners.HubListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeleteCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (ServerMonitor.getInstance().hasServer(p.getUniqueId())) {
                if (!ServerCMD.commandCooldown.contains(p)) {
                    HubListener.deleteServer(p);
                    p.resetTitle();
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&', ""), ChatColor.translateAlternateColorCodes('&', "&aYour SMP has been deleted!"), 0, 60, 10);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYour SMP has been deleted!"));
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot delete your SMP while the server is running!"));
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have an SMP! Create a server using &b/smp"));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed!"));
        }

        return false;
    }
}

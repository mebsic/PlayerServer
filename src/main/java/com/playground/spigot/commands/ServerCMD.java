package com.playground.spigot.commands;

import com.playground.spigot.PlayerServer;
import com.playground.spigot.ServerMonitor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class ServerCMD implements CommandExecutor {

    public static List<Player> commandCooldown = new ArrayList<>();
    public PlayerServer plugin;

    public ServerCMD(PlayerServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (!ServerMonitor.getInstance().hasServer(p.getUniqueId())) {
                commandCooldown.add(p);
                ServerMonitor.getInstance().addBungeeServer(p);
                p.resetTitle();
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', ""), ChatColor.translateAlternateColorCodes('&', "&7Creating your SMP..."), 0, 72000, 10);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Creating your SMP..."));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThis may take a few minutes, please wait..."));
            } else {
                if (!commandCooldown.contains(p)) {
                    if (ServerMonitor.getInstance().isPortAvailable(ServerMonitor.getInstance().getPlayerPort(p.getUniqueId()))) {
                        commandCooldown.add(p);
                        ServerMonitor.getInstance().startServer(p);
                        p.resetTitle();
                        p.sendTitle(ChatColor.translateAlternateColorCodes('&', ""), ChatColor.translateAlternateColorCodes('&', "&7Starting your SMP..."), 0, 72000, 10);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Starting your SMP, please wait..."));
                    } else {
                        commandCooldown.add(p);
                        PlayerServer.getInstance().sendPlayer(p, p.getUniqueId().toString(), 60);
                        p.resetTitle();
                        p.sendTitle(ChatColor.translateAlternateColorCodes('&', ""), ChatColor.translateAlternateColorCodes('&', "&aTeleporting..."), 0, 60, 10);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYour SMP is already online, teleporting you..."));
                    }
                } else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour SMP is already running! You will be sent automatically when the server is available."));
                }
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed!"));
        }

        return false;
    }
}

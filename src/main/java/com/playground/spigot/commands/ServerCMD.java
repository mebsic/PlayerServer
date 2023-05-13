package com.playground.spigot.commands;

import com.playground.spigot.PlayerServer;
import com.playground.spigot.ServerMonitor;
import com.playground.spigot.listeners.HubListener;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class ServerCMD implements CommandExecutor {

    public static ArrayList<Player> commandCooldown = new ArrayList<>();
    public PlayerServer plugin;

    public ServerCMD(PlayerServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 0) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);

                if (!ServerMonitor.getInstance().hasServer(p.getUniqueId())) {
                    commandCooldown.add(p);
                    HubListener.stopServer(p);
                    HubListener.deleteServerDirectory(p);
                    ServerMonitor.getInstance().addBungeeServer(p);
                    p.resetTitle();
                    p.sendTitle("", ChatColor.translateAlternateColorCodes('&', "&7Creating your SMP..."), 0, Integer.MAX_VALUE, 0);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Creating your SMP..."));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThis may take a few minutes, please wait..."));
                } else {
                    if (!commandCooldown.contains(p)) {
                        commandCooldown.add(p);
                        p.resetTitle();
                        if (ServerMonitor.getInstance().isPortAvailable(ServerMonitor.getInstance().getPlayerPort(p.getUniqueId()))) {
                            ServerMonitor.getInstance().startServer(p);
                            p.sendTitle("", ChatColor.translateAlternateColorCodes('&', "&7Starting your SMP..."), 0, Integer.MAX_VALUE, 0);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Starting your SMP, please wait..."));
                        } else {
                            PlayerServer.getInstance().sendPlayer(p, p.getUniqueId().toString(), 60);
                            p.sendTitle("", ChatColor.translateAlternateColorCodes('&', "&aTeleporting..."), 0, Integer.MAX_VALUE, 0);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYour SMP is already online, teleporting you..."));
                        }
                    } else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour SMP is already loading! You will be teleported when the server is available."));
                    }
                }
            } else {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid command! Correct usage: &b/smp"));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed!"));
        }

        return false;
    }
}

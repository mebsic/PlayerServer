package com.playground.bungee.commands;

import com.playground.bungee.BungeeManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCMD extends Command {

    public MaintenanceCMD() {
        super("maintenance");
    }

    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length == 0) {
                if (!BungeeManager.getInstance().getSqlMaintenanceManager().isEnabled()) {
                    BungeeManager.getInstance().getSqlMaintenanceManager().setEnabled(true);
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (!BungeeManager.getInstance().getSqlWhitelistManager().exists(p.getUniqueId())) {
                            p.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&6Maintenance mode is now enabled!")));
                        }
                    }
                    ProxyServer.getInstance().broadcast(new ComponentBuilder("Maintenance mode is now enabled!").color(ChatColor.GOLD).create());
                } else {
                    BungeeManager.getInstance().getSqlMaintenanceManager().setEnabled(false);
                    ProxyServer.getInstance().broadcast(new ComponentBuilder("Maintenance mode is now disabled!").color(ChatColor.RED).create());
                }
            } else {
                sender.sendMessage(new ComponentBuilder("Invalid command! Correct usage: maintenance").color(ChatColor.RED).create());
            }
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            player.sendMessage(new ComponentBuilder("You don't have permission to run that command!").color(ChatColor.RED).create());
        }
    }
}

package com.playground.bungee.commands;

import com.playground.bungee.BungeeManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import java.util.UUID;

public class WhitelistCMD extends Command {

    public WhitelistCMD() {
        super("whitelist");
    }

    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length == 1) {
                if (!BungeeManager.getInstance().getSqlMaintenanceManager().isEnabled()) {
                    sender.sendMessage(new ComponentBuilder("Maintenance mode is not enabled!").color(ChatColor.RED).create());
                } else {
                    UUID uuid = null;

                    try {
                        uuid = BungeeManager.getInstance().findPlayer(args[0]);
                    } catch (Exception ignored) {}

                    if (uuid != null && !BungeeManager.getInstance().getSqlWhitelistManager().exists(uuid)) {
                        BungeeManager.getInstance().getSqlWhitelistManager().addPlayer(uuid);
                        ProxyServer.getInstance().broadcast(new ComponentBuilder(args[0] + " has been added to the maintenance whitelist!").color(ChatColor.GREEN).create());
                    } else if (uuid != null && BungeeManager.getInstance().getSqlWhitelistManager().exists(uuid)) {
                        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
                        BungeeManager.getInstance().getSqlWhitelistManager().removePlayer(uuid);

                        if (player != null && player.isConnected()) {
                            player.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cYou were removed from the maintenance whitelist!")));
                        }
                        ProxyServer.getInstance().broadcast(new ComponentBuilder(args[0] + " was removed from the maintenance whitelist!").color(ChatColor.RED).create());
                    } else {
                        sender.sendMessage(new ComponentBuilder(args[0] + " was not found! Please try again.").color(ChatColor.RED).create());
                    }
                }
            } else {
                sender.sendMessage(new ComponentBuilder("Invalid command! Correct usage: whitelist <player>").color(ChatColor.RED).create());
            }
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            player.sendMessage(new ComponentBuilder("You don't have permission to run that command!").color(ChatColor.RED).create());
        }
    }
}

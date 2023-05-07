package com.playground.spigot.listeners;

import com.playground.spigot.PlayerServer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

public class ProcessListener implements Listener {

    private final String[] blockedPunishCommands = {
            "/ban",
            "/kick",
            "/pardon",
            "/minecraft:ban",
            "/minecraft:kick",
            "/minecraft:pardon"
    };

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getUniqueId().equals(PlayerServer.getServerName())) {
            e.allow();
        } else if (!PlayerServer.getInstance().getSqlInviteManager().exists(e.getUniqueId(), PlayerServer.getServerName())) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', "&cYou are not invited to this SMP!"));
        } else {
            e.allow();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().resetTitle();
        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', "&7" + e.getPlayer().getDisplayName() + " &ejoined the SMP"));

        if (e.getPlayer().getUniqueId().equals(PlayerServer.getServerName())) {
            e.getPlayer().setOp(true);

            if (PlayerServer.getInstance().getSqlServerManager().isCreating(e.getPlayer().getUniqueId())) {
                PlayerServer.getInstance().getSqlServerManager().setCreating(e.getPlayer().getUniqueId(), false);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&6Welcome to the SMP! Invite your friends using &b/invite\n&6To return to the hub, type &a/hub"));
                }
            }.runTaskLaterAsynchronously(PlayerServer.getInstance(), 40L);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        e.getPlayer().resetTitle();
        PlayerServer.getInstance().checkForShutdown();
        e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', "&7" + e.getPlayer().getDisplayName() + " &eleft the SMP"));
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.getReason().contains("Flying is not enabled on this server") || e.getReason().equals("disconnect.spam") || e.getReason().contains("Kicked for spamming")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        if ((e.getPlayer().getUniqueId().equals(PlayerServer.getServerName())
                && (e.getMessage().startsWith("/invite")
                || e.getMessage().startsWith("/remove")
                || e.getMessage().startsWith("/playerserver:invite")
                || e.getMessage().startsWith("/playerserver:remove"))
                && (e.getMessage().contains(e.getPlayer().getName())
                || e.getMessage().contains("@")))
                || e.getMessage().startsWith("/minecraft:stop")
                || e.getMessage().startsWith("/minecraft:whitelist")
                || e.getMessage().startsWith("/minecraft:reload")
                || e.getMessage().startsWith("/minecraft:datapack")
                || e.getMessage().startsWith("/whitelist")
                || e.getMessage().startsWith("/datapack")
                || e.getMessage().startsWith("/stop")
                || e.getMessage().startsWith("/restart")
                || e.getMessage().startsWith("/reload")
                || e.getMessage().startsWith("/rl")
                || e.getMessage().startsWith("/bukkit")
                || e.getMessage().startsWith("/paper")
                || e.getMessage().startsWith("/spigot")
                || e.getMessage().startsWith("/smp")
                || e.getMessage().startsWith("/delete")
                || e.getMessage().startsWith("/playerserver:smp")
                || e.getMessage().startsWith("/playerserver:delete")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to run that command!"));
        }

        for (String x : blockedPunishCommands) {
            if ((!e.getPlayer().getUniqueId().equals(PlayerServer.getServerName())
                    && e.getMessage().contains(x))
                    || (e.getPlayer().getUniqueId().equals(PlayerServer.getServerName())
                    && e.getMessage().contains(x)
                    && (e.getMessage().contains(e.getPlayer().getName())
                    || e.getMessage().contains("@")))) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to run that command!"));
            }
        }

        if (e.getMessage().startsWith("/ban-ip")
                || e.getMessage().startsWith("/pardon-ip")
                || e.getMessage().startsWith("/minecraft:ban-ip")
                || e.getMessage().startsWith("/minecraft:pardon-ip")) {
            e.setCancelled(true);
        }
    }
}

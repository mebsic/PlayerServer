package com.playground.spigot.listeners;

import com.playground.spigot.PlayerServer;
import com.playground.spigot.commands.ServerCMD;
import com.playground.spigot.ServerMonitor;
import com.google.common.base.Charsets;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import java.io.File;
import java.io.IOException;

public class HubListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().resetTitle();
        e.getPlayer().teleport(Bukkit.getWorld("world").getSpawnLocation());
        setEffects(e.getPlayer());
        e.setJoinMessage(null);
        checkIfCreatingServer(e.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        for (PotionEffect effect : e.getPlayer().getActivePotionEffects()) {
            e.getPlayer().removePotionEffect(effect.getType());
        }
        e.setQuitMessage(null);
        e.getPlayer().resetTitle();
        endProcessInterruptedStart(e.getPlayer());
        checkIfCreatingServer(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.getReason().contains("Flying is not enabled on this server") || e.getReason().equals("disconnect.spam") || e.getReason().contains("Kicked for spamming")) {
            e.setCancelled(true);
        }
    }

    public static void endProcessInterruptedStart(Player p) {
        if (ServerMonitor.getInstance().status.containsKey(p.getUniqueId())) {
            stopServer(p);
        }
        reset(p);
    }

    private static void reset(Player p) {
        boolean isInProgress = ServerMonitor.getInstance().taskID.containsKey(p.getUniqueId()) || ServerMonitor.getInstance().connectionAttempts.containsKey(p.getUniqueId());

        if (ServerCMD.commandCooldown.contains(p) && isInProgress) {
            ServerCMD.commandCooldown.remove(p);
        }

        if (ServerMonitor.newServerCooldown.contains(p) && isInProgress) {
            ServerMonitor.newServerCooldown.remove(p);
        }
        removeFromCollection(p);
    }

    public static void deleteServer(Player p) {
        String serverDeletedMessage = "&cThe SMP you were playing on was deleted by &b" + p.getName() + "&c!";
        p.sendPluginMessage(PlayerServer.getInstance(), "bungeecord:remove_server", serverDeletedMessage.getBytes(Charsets.UTF_8));

        PlayerServer.getInstance().getSqlInviteManager().deleteInvites(p.getUniqueId());
        PlayerServer.getInstance().getSqlServerManager().remove(p.getUniqueId());
        stopServer(p);
        deleteServerDirectory(p);
        deleteServerFromConfig(p);
    }

    private static void deleteServerFromConfig(Player p) {
        String uuidString = p.getUniqueId().toString();
        File bungeeFile = new File(PlayerServer.getInstance().bungeeConfigLocation);
        FileConfiguration bungeeConfig = new YamlConfiguration();

        try {
            bungeeConfig.load(bungeeFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (bungeeConfig.getConfigurationSection("servers").getKeys(false).contains(uuidString)) {
            bungeeConfig.getConfigurationSection("servers").set(uuidString, null);

            try {
                bungeeConfig.save(bungeeFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void removeFromCollection(Player p) {

        if (ServerMonitor.getInstance().taskID.get(p.getUniqueId()) != null) {
            int tid = ServerMonitor.getInstance().taskID.get(p.getUniqueId());

            PlayerServer.getInstance().getServer().getScheduler().cancelTask(tid);
            ServerMonitor.getInstance().taskID.remove(p.getUniqueId());
            ServerMonitor.getInstance().connectionAttempts.remove(p.getUniqueId());
        }
    }

    public static void deleteServerDirectory(Player p) {
        try {
            Runtime.getRuntime().exec(PlayerServer.getInstance().scriptsDirectory + "/deleteserver.sh " + p.getUniqueId());
        } catch (IOException ignored) {}
    }

    private static void stopServer(Player p) {
        try {
            Runtime.getRuntime().exec(PlayerServer.getInstance().scriptsDirectory + "/stopserver.sh " + p.getUniqueId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkIfCreatingServer(Player p) {
        if (ServerMonitor.getInstance().isCreatingServer(p.getUniqueId())) {
            deleteServer(p);
        }
    }

    private void setEffects(Player p) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.hidePlayer(PlayerServer.getInstance(), p);
            p.hidePlayer(PlayerServer.getInstance(), players);
        }

        p.setGameMode(GameMode.ADVENTURE);
        p.setFlying(false);
        p.setAllowFlight(false);
        p.setHealth(20.0f);
        p.setFoodLevel(20);
        p.setWalkSpeed(0.0f);
        p.setFlySpeed(0.0f);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = p.getLocation();

        if (loc.getBlockY() <= 0) {
            p.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {

        if (e.getMessage().startsWith("/kill")
                || e.getMessage().startsWith("/fill")
                || e.getMessage().startsWith("/place")
                || e.getMessage().startsWith("/setblock")
                || e.getMessage().startsWith("/clone")
                || e.getMessage().startsWith("/function")
                || e.getMessage().startsWith("/jfr")
                || e.getMessage().startsWith("/execute")
                || e.getMessage().startsWith("/trigger")
                || e.getMessage().startsWith("/data")
                || e.getMessage().startsWith("/datapack")
                || e.getMessage().startsWith("/bossbar")
                || e.getMessage().startsWith("/scoreboard")
                || e.getMessage().startsWith("/worldborder")
                || e.getMessage().startsWith("/stop")
                || e.getMessage().startsWith("/restart")
                || e.getMessage().startsWith("/reload")
                || e.getMessage().startsWith("/whitelist")
                || e.getMessage().startsWith("/rl")
                || e.getMessage().startsWith("/bukkit")
                || e.getMessage().startsWith("/paper")
                || e.getMessage().startsWith("/spigot")
                || e.getMessage().startsWith("/minecraft:")
                || e.getMessage().startsWith("/opme")
                || e.getMessage().startsWith("/invite")
                || e.getMessage().startsWith("/remove")
                || e.getMessage().startsWith("/credits")
                || e.getMessage().startsWith("/playerserver:opme")
                || e.getMessage().startsWith("/playerserver:invite")
                || e.getMessage().startsWith("/playerserver:remove")
                || e.getMessage().startsWith("/playerserver:credits")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission to run that command!"));
        }

        if (ServerMonitor.newServerCooldown.contains(e.getPlayer())
                && (e.getMessage().startsWith("/smp")
                || e.getMessage().startsWith("/delete")
                || e.getMessage().startsWith("/playerserver:smp")
                || e.getMessage().startsWith("/playerserver:delete"))) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour SMP is being created! Please wait before running commands."));
        }
    }

    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        e.setTo(e.getFrom());
    }
}

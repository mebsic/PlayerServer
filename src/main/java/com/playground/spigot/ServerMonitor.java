package com.playground.spigot;

import com.playground.spigot.commands.ServerCMD;
import com.playground.spigot.listeners.HubListener;
import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.util.*;

@SuppressWarnings("deprecation")
public class ServerMonitor {

    public Map<UUID, Integer> connectionAttempts = new HashMap<>(), taskID = new HashMap<>();
    public static List<Player> newServerCooldown = new ArrayList<>();
    public static ServerMonitor instance = new ServerMonitor();
    public Map<UUID, ServerStatus> status = new HashMap<>();

    public enum ServerStatus {
        STARTING
    }

    public static ServerMonitor getInstance() {
        return instance;
    }

    public boolean isServerOnline(UUID uuid, int port) {
        return PlayerServer.getInstance().getSqlPlayerManager().isOnline(uuid, port);
    }

    public int getPlayerPort(UUID uuid) {
        return PlayerServer.getInstance().getSqlPlayerManager().getPort(uuid);
    }

    public boolean hasServer(UUID uuid) {
        return PlayerServer.getInstance().getSqlPlayerManager().exists(uuid);
    }

    public void addBungeeServer(Player p) {
        newServerCooldown.add(p);
        UUID playerID = p.getUniqueId();
        File bungeeFile = new File(PlayerServer.getInstance().bungeeConfigLocation);
        FileConfiguration bungeeConfig = new YamlConfiguration();

        try {
            int port = PlayerServer.getInstance().getSqlPortManager().updateCurrentPort();
            bungeeConfig.load(bungeeFile);
            bungeeConfig.set("servers." + playerID + ".address", "localhost:" + port);
            bungeeConfig.set("servers." + playerID + ".motd", p.getName() + "'s server");
            bungeeConfig.set("servers." + playerID + ".restricted", false);
            bungeeConfig.save(bungeeFile);
            copyNewServer(p);
            p.sendPluginMessage(PlayerServer.getInstance(), "bungeecord:add", String.valueOf(port).getBytes(Charsets.UTF_8));
            setupServer(p, port);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void copyNewServer(Player p) {
        try {
            Runtime.getRuntime().exec(PlayerServer.getInstance().scriptsDirectory + "/newserver.sh " + p.getUniqueId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupServer(final Player p, final int port) {
        new BukkitRunnable() {
            public void run() {
                try {
                    FileInputStream in = new FileInputStream(PlayerServer.getInstance().playerServerDirectory.replace("%PLAYER", p.getUniqueId().toString()));
                    Properties prop = new Properties();
                    prop.load(in);
                    in.close();

                    FileOutputStream out = new FileOutputStream(PlayerServer.getInstance().playerServerDirectory.replace("%PLAYER", p.getUniqueId().toString()));
                    prop.setProperty("server-port", String.valueOf(port));
                    prop.store(out, null);
                    out.close();

                    FileWriter op = new FileWriter(PlayerServer.getInstance().opsDirectory.replace("%PLAYER", p.getUniqueId().toString()));
                    final JSONObject jsonOps = new JSONObject();
                    jsonOps.put("uuid", p.getUniqueId().toString());
                    jsonOps.put("name", p.getName());
                    jsonOps.put("level", 4);
                    jsonOps.put("bypassesPlayerLimit", false);
                    op.write("[" + jsonOps.toJSONString() + "]");
                    op.close();

                    FileWriter whitelist = new FileWriter(PlayerServer.getInstance().whitelistDirectory.replace("%PLAYER", p.getUniqueId().toString()));
                    final JSONObject jsonWhitelist = new JSONObject();
                    jsonWhitelist.put("uuid", p.getUniqueId().toString());
                    jsonWhitelist.put("name", p.getName());
                    whitelist.write("[" + jsonWhitelist.toJSONString() + "]");
                    whitelist.close();

                    PlayerServer.getInstance().getSqlPlayerManager().createServer(p.getUniqueId(), port, false);

                    if (ServerMonitor.getInstance().hasServer(p.getUniqueId())) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    status.putIfAbsent(p.getUniqueId(), ServerStatus.STARTING);
                                    String playerID = p.getUniqueId().toString();
                                    Runtime.getRuntime().exec(PlayerServer.getInstance().scriptsDirectory + "/startserver.sh " + playerID);
                                    joinServerAfterStart(p, getPlayerPort(p.getUniqueId()), 3600, 3600);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.runTaskLaterAsynchronously(PlayerServer.getInstance(), 20);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLaterAsynchronously(PlayerServer.getInstance(), 120);
    }

    public boolean isPortAvailable(int port) {
        boolean available = false;
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(port);
            available = true;
        } catch (IOException ignored) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return available;
    }

    public void startServer(final Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    status.putIfAbsent(p.getUniqueId(), ServerStatus.STARTING);
                    String playerID = p.getUniqueId().toString();
                    // PlayerServer might not set online status to false if server crashes or VPS restarts
                    // if server should not be online, set online to false in the database and start the server
                    if (isServerOnline(p.getUniqueId(), getPlayerPort(p.getUniqueId()))) {
                        PlayerServer.getInstance().getSqlPlayerManager().setOnline(p.getUniqueId(), false);
                    }
                    Runtime.getRuntime().exec(PlayerServer.getInstance().scriptsDirectory + "/startserver.sh " + playerID);
                    joinServerAfterStart(p, getPlayerPort(p.getUniqueId()), 1800, 1800);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLaterAsynchronously(PlayerServer.getInstance(), 20);
    }

    private void joinServerAfterStart(Player p, int port, long delay, long period) {

        connectionAttempts.put(p.getUniqueId(), 0);
        final int id = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(PlayerServer.getInstance(), () -> {
            boolean online = false;
            int attempts;

            if (isServerOnline(p.getUniqueId(), port)) {
                online = true;
            }
            attempts = connectionAttempts.computeIfPresent(p.getUniqueId(), (k, v) -> v + 1);

            if (!online && (attempts >= 1 && attempts <= 5)) {
                p.resetTitle();
                if (newServerCooldown.contains(p)) {
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&', ""), ChatColor.translateAlternateColorCodes('&', "&7Creating your SMP..."), 0, 72000, 10);
                } else {
                    p.sendTitle(ChatColor.translateAlternateColorCodes('&', ""), ChatColor.translateAlternateColorCodes('&', "&7Starting your SMP..."), 0, 72000, 10);
                }
            } else if (online && (attempts >= 1 && attempts <= 5)) {
                status.remove(p.getUniqueId());
                PlayerServer.getInstance().sendPlayer(p, p.getUniqueId().toString(), 20);
            } else {
                p.resetTitle();
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', ""), ChatColor.translateAlternateColorCodes('&', "&cFailed to join your SMP!"), 0, 60, 10);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed to join your SMP! Please try again later."));
                HubListener.endProcessInterruptedStart(p);
            }
        }, delay, period);
        taskID.putIfAbsent(p.getUniqueId(), id);
    }
}

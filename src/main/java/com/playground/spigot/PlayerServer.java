package com.playground.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playground.spigot.commands.*;
import com.playground.spigot.listeners.HubListener;
import com.playground.spigot.listeners.ProcessListener;
import com.playground.sql.MySQL;
import com.playground.sql.managers.spigot.SQLInviteManager;
import com.playground.sql.managers.spigot.SQLServerManager;
import com.playground.sql.managers.spigot.SQLPortManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class PlayerServer extends JavaPlugin {

    public String serverType, playerServerDirectory, bungeeConfigLocation, scriptsDirectory, whitelistDirectory, opsDirectory;
    public SQLInviteManager sqlInviteManager;
    public SQLServerManager sqlServerManager;
    public SQLPortManager sqlPortManager;
    public static PlayerServer instance;
    public FileConfiguration mysqlYml;
    public static UUID serverName;
    public MySQL SQL;

    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getDatabaseConfigProperties();

        if (serverType.equalsIgnoreCase("lobby")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeecord:add_server");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeecord:remove_server");
            getCommand("smp").setExecutor(new ServerCMD(this));
            getCommand("delete").setExecutor(new DeleteCMD());
            Bukkit.getPluginManager().registerEvents(new HubListener(), this);
        } else if (serverType.equalsIgnoreCase("smp")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeecord:uninvite_player");
            serverName = UUID.fromString(Paths.get(Bukkit.getWorldContainer().getAbsolutePath()).getParent().getFileName().toString());
            Bukkit.getPluginManager().registerEvents(new ProcessListener(), this);
            getCommand("opme").setExecutor(new OpmeCMD());
            getCommand("invite").setExecutor(new InviteCMD());
            getCommand("remove").setExecutor(new RemoveCMD());
            getCommand("credits").setExecutor(new CreditsCMD());
            checkForShutdown();
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid configuration!");
            getServer().shutdown();
        }
        connectToDatabase();
    }

    @Override
    public void onDisable() {
        if (serverType.equalsIgnoreCase("lobby")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                HubListener.endProcessInterruptedStart(p);
            }
        } else if (serverType.equalsIgnoreCase("smp")) {
            getSqlPlayerManager().setOnline(getServerName(), false);
        }
        disconnectAndSave();
    }

    public static PlayerServer getInstance() {
        return instance;
    }

    public static UUID getServerName() {
        return serverName;
    }

    public FileConfiguration getCustomConfig() {
        return mysqlYml;
    }

    public SQLServerManager getSqlPlayerManager() {
        return sqlServerManager;
    }

    public SQLPortManager getSqlPortManager() {
        return sqlPortManager;
    }

    public SQLInviteManager getSqlInviteManager() {
        return sqlInviteManager;
    }

    private void disconnectAndSave() {
        SQL.disconnect();
        saveConfig();
    }

    private void connectToDatabase() {
        this.SQL = new MySQL();
        this.sqlPortManager = new SQLPortManager(this);
        this.sqlServerManager = new SQLServerManager(this);
        this.sqlInviteManager = new SQLInviteManager(this);

        try {
            SQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot connect to database!");
        }

        if (SQL.isConnected()) {
            Bukkit.getLogger().log(Level.INFO, "Connected to database!");
            if (serverType.equalsIgnoreCase("lobby")) {
                getSqlPortManager().createCurrentPortTable();
                getSqlPlayerManager().createServersTable();
                getSqlInviteManager().createInvitesTable();
            } else if (serverType.equalsIgnoreCase("smp")) {
                getSqlPlayerManager().setOnline(getServerName(), true);
            }
        }
    }

    public void loadConfigs() {
        loadDatabaseConfig();
        saveDefaultConfig();
        saveConfig();
        serverType = getConfig().getString("server-type");
        playerServerDirectory = getConfig().getString("player-server-directory");
        bungeeConfigLocation = getConfig().getString("bungee-config-location");
        scriptsDirectory = getConfig().getString("scripts-directory");
        whitelistDirectory = getConfig().getString("whitelist-directory");
        opsDirectory = getConfig().getString("ops-directory");
    }

    private void loadDatabaseConfig() {
        File mysql = new File(getDataFolder(), "mysql.yml");
        if (!mysql.exists()) {
            mysql.getParentFile().mkdirs();
            saveResource("mysql.yml", false);
        }
        mysqlYml = YamlConfiguration.loadConfiguration(mysql);
    }

    private void getDatabaseConfigProperties() {
        MySQL.host = getCustomConfig().getString("host");
        MySQL.port = getCustomConfig().getString("port");
        MySQL.database = getCustomConfig().getString("database");
        MySQL.username = getCustomConfig().getString("username");
        MySQL.password = getCustomConfig().getString("password");
        MySQL.useSSL = getCustomConfig().getBoolean("useSSL");
    }

    public void sendPlayer(final Player p, final String server, final int ticks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                p.resetTitle();
                p.sendTitle("", ChatColor.translateAlternateColorCodes('&', "&7Joining SMP..."), 0, Integer.MAX_VALUE, 0);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Joining SMP..."));
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(server);
                p.sendPluginMessage(getInstance(), "BungeeCord", out.toByteArray());
            }
        }.runTaskLaterAsynchronously(this, ticks);
    }

    public void checkForShutdown() {
        if (getServer().getOnlinePlayers().size() <= 1) {
            getServer().getScheduler().scheduleAsyncDelayedTask(this, this::checkPlayers, 60 * 1200);
        }
    }

    private void checkPlayers() {
        if (getServer().getOnlinePlayers().size() <= 0) {
            getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c[ALERT]"));
            getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eNo players have logged in for &a1 hour&e!"));
            getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7Shutting down..."));
            getServer().shutdown();
        }
    }

    public UUID findPlayer(String s) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + s).openStream()));
        String uuid = (((JsonObject) new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
        uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
        in.close();

        return UUID.fromString(uuid);
    }
}

package com.playground.bungee;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.playground.bungee.commands.MaintenanceCMD;
import com.playground.bungee.commands.WhitelistCMD;
import com.playground.sql.MySQL;
import com.playground.sql.managers.bungee.SQLMaintenanceManager;
import com.playground.sql.managers.bungee.SQLWhitelistManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class BungeeManager extends Plugin implements Listener {

    private final String sendFallbackServerFailedMessage = "\n\n&cCould not send you to a fallback server!\nTo play again, please reconnect to &b";
    public SQLMaintenanceManager sqlMaintenanceManager;
    public SQLWhitelistManager sqlWhitelistManager;
    private Configuration sqlConfig;
    private Configuration motdConfig;
    private Configuration serverIPConfig;
    public static Plugin instance;
    public MySQL SQL;

    public static BungeeManager getInstance() {
        return (BungeeManager) instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        connectToDatabase();

        getProxy().registerChannel("bungeecord:add_server");
        getProxy().registerChannel("bungeecord:remove_server");
        getProxy().registerChannel("bungeecord:uninvite_player");
        getProxy().getPluginManager().registerListener(this, this);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MaintenanceCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new WhitelistCMD());
    }

    @Override
    public void onDisable() {
        disconnectFromDatabase();
    }

    private void loadConfigs() {
        sqlConfig = loadUniqueConfig("mysql.yml");
        motdConfig = loadUniqueConfig("motd.yml");
        serverIPConfig = loadUniqueConfig("info.yml");
        getDatabaseConfigProperties();
    }

    private Configuration loadUniqueConfig(String filename) {
        Configuration config = null;

        try {
            File configFile = new File(getDataFolder(), filename);
            if (!configFile.exists()) {
                getDataFolder().mkdir();
                Files.copy(this.getResourceAsStream(filename), configFile.toPath());
            }

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), filename));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), filename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return config;
    }

    private void getDatabaseConfigProperties() {
        MySQL.host = getCustomSqlConfig().getString("host");
        MySQL.port = getCustomSqlConfig().getString("port");
        MySQL.database = getCustomSqlConfig().getString("database");
        MySQL.username = getCustomSqlConfig().getString("username");
        MySQL.password = getCustomSqlConfig().getString("password");
        MySQL.useSSL = getCustomSqlConfig().getBoolean("useSSL");
    }

    private void connectToDatabase() {
        this.SQL = new MySQL();
        this.sqlMaintenanceManager = new SQLMaintenanceManager(this);
        this.sqlWhitelistManager = new SQLWhitelistManager(this);

        try {
            SQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            getLogger().log(Level.SEVERE, "Cannot connect to database!");
            getProxy().stop();
        }

        if (SQL.isConnected()) {
            getLogger().log(Level.INFO, "Connected to database!");
            getSqlMaintenanceManager().createMaintenanceTable();
            getSqlWhitelistManager().createWhitelistTable();
        }
    }

    @EventHandler
    public void onPluginMessageConstructServerInfo(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("bungeecord:add_server")) {
            return;
        }
        if (e.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();
            String port = new String(e.getData(), Charsets.UTF_8);
            ServerInfo info = ProxyServer.getInstance().getServers().get(p.getUniqueId().toString());

            if (info != null) {
                ProxyServer.getInstance().getServers().remove(info.getName());
            }
            info = ProxyServer.getInstance().constructServerInfo(p.getUniqueId().toString(), new InetSocketAddress("localhost", Integer.parseInt(port)), p.getName() + "'s server", false);
            ProxyServer.getInstance().getServers().putIfAbsent(p.getUniqueId().toString(), info);
        }
    }

    @EventHandler
    public void onPluginMessageDeconstructServerInfo(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("bungeecord:remove_server")) {
            return;
        }
        if (e.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();
            ServerInfo info = ProxyServer.getInstance().getServers().get(p.getUniqueId().toString());
            String message = new String(e.getData(), Charsets.UTF_8);

            for (ProxiedPlayer player : info.getPlayers()) {
                sendToFallbackServer(player, message);
            }
            ProxyServer.getInstance().getServers().remove(info.getName());
        }
    }

    @EventHandler
    public void onPluginMessageRemovePlayerFromSMP(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("bungeecord:uninvite_player")) {
            return;
        }
        if (e.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();
            String message = new String(e.getData(), Charsets.UTF_8);
            sendToFallbackServer(p, message);
        }
    }

    private void sendToFallbackServer(ProxiedPlayer player, String message) {
        getProxy().getServers().get(player.getPendingConnection().getListener().getFallbackServer()).ping((result, error) -> {
            if (error != null) {
                player.disconnect(ChatColor.translateAlternateColorCodes('&', message + sendFallbackServerFailedMessage + getCustomServerIPConfig().getString("server-ip")));
            } else {
                player.connect(ProxyServer.getInstance().getServers().get(player.getPendingConnection().getListener().getFallbackServer()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        });
    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        ServerPing ping = e.getResponse();

        if (getSqlMaintenanceManager().isEnabled()) {
            ping.setDescription(ChatColor.translateAlternateColorCodes('&', "&cMaintenance mode"));
            ping.setVersion(new ServerPing.Protocol(ChatColor.translateAlternateColorCodes('&', "&4Maintenance"), ping.getVersion().getProtocol() - 5));
        } else {
            motdConfig = loadUniqueConfig("motd.yml");
            ping.setDescription(ChatColor.translateAlternateColorCodes('&', getCustomMotdConfig().getString("header") + "\n" + getCustomMotdConfig().getString("footer")));
        }

        e.setResponse(ping);
    }

    @EventHandler
    public void onConnect(ServerConnectEvent e) {
        UUID uuid = null;

        if (getSqlMaintenanceManager().isEnabled() && !getSqlWhitelistManager().exists(e.getPlayer().getUniqueId())) {
            e.getPlayer().disconnect(ChatColor.translateAlternateColorCodes('&', "&6This server is currently in maintenance mode!"));
        } else {
            try {
                uuid = findPlayer(e.getPlayer().getName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (uuid != null && !e.getPlayer().getUniqueId().toString().equals(uuid.toString())) {
                e.getPlayer().disconnect(ChatColor.translateAlternateColorCodes('&', "&cInvalid login!"));
            }
        }
    }

    public UUID findPlayer(String s) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + s).openStream()));
        final String uuid = (((JsonObject) new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
        final String realUUID = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
        in.close();

        return UUID.fromString(realUUID);
    }

    public Configuration getCustomMotdConfig() {
        return motdConfig;
    }

    public Configuration getCustomServerIPConfig() {
        return serverIPConfig;
    }

    public Configuration getCustomSqlConfig() {
        return sqlConfig;
    }

    public SQLMaintenanceManager getSqlMaintenanceManager() {
        return sqlMaintenanceManager;
    }

    public SQLWhitelistManager getSqlWhitelistManager() {
        return sqlWhitelistManager;
    }

    private void disconnectFromDatabase() {
        SQL.disconnect();
    }
}

package com.playground.bungee;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class BungeeManager extends Plugin implements Listener {

    private final String sendFallbackServerFailedMessage = "\n\n&cCould not send you to a fallback server!\nTo play again, please reconnect to &bexample.com";
    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getProxy().registerChannel("bungeecord:ping");
        getProxy().registerChannel("bungeecord:add");
        getProxy().registerChannel("bungeecord:remove");
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onPluginMessageConstructServerInfo(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("bungeecord:add")) {
            return;
        }
        if (e.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();
            String port = new String(e.getData(), Charsets.UTF_8);
            ServerInfo info = ProxyServer.getInstance().constructServerInfo(p.getUniqueId().toString(), new InetSocketAddress("localhost", Integer.parseInt(port)), p.getName() + "'s server", false);
            ProxyServer.getInstance().getServers().putIfAbsent(p.getUniqueId().toString(), info);
        }
    }

    @EventHandler
    public void onPluginMessageDeconstructServerInfo(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("bungeecord:remove")) {
            return;
        }
        if (e.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();
            ServerInfo info = ProxyServer.getInstance().getServers().get(p.getUniqueId().toString());
            String message = new String(e.getData(), Charsets.UTF_8);
            for (ProxiedPlayer player : info.getPlayers()) {
                sendToFallbackServer(player, message);
            }
            ProxyServer.getInstance().getServers().remove(p.getUniqueId().toString());
        }
    }

    @EventHandler
    public void onPluginMessageRemovePlayerFromSMP(PluginMessageEvent e) {
        if (!e.getTag().equalsIgnoreCase("bungeecord:ping")) {
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
                player.disconnect(ChatColor.translateAlternateColorCodes('&', message + sendFallbackServerFailedMessage));
            } else {
                player.connect(ProxyServer.getInstance().getServers().get(player.getPendingConnection().getListener().getFallbackServer()));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        });
    }

    @EventHandler
    public void onConnect(ServerConnectEvent e) {
        UUID uuid = null;
        try {
            uuid = getUUID(e.getPlayer().getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!e.getPlayer().getUniqueId().toString().equals(uuid.toString())) {
            e.getPlayer().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cInvalid login!")));
        }
    }

    private UUID getUUID(String s) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + s).openStream()));
        final String uuid = (((JsonObject) new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
        final String realUUID = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
        in.close();

        return UUID.fromString(realUUID);
    }
}

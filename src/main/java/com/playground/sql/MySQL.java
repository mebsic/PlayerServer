package com.playground.sql;

import com.playground.spigot.PlayerServer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private final String host = PlayerServer.getInstance().getCustomConfig().getString("host");
    private final String port = PlayerServer.getInstance().getCustomConfig().getString("port");
    private final String database = PlayerServer.getInstance().getCustomConfig().getString("database");
    private final String username = PlayerServer.getInstance().getCustomConfig().getString("username");
    private final String password = PlayerServer.getInstance().getCustomConfig().getString("password");
    private final boolean useSSL = PlayerServer.getInstance().getCustomConfig().getBoolean("useSSL");
    private Connection connection;
    private final String connectionURL = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL;

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        if (!isConnected()) {
            connection = DriverManager.getConnection(connectionURL, username, password);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.playground.sql.managers;

import com.playground.spigot.PlayerServer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLPortManager {

    private final int DEFAULT_PORT = 25566;
    private final PlayerServer plugin;

    public SQLPortManager(PlayerServer plugin) {
        this.plugin = plugin;
    }

    public void createCurrentPortTable() {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS current_port(port INT UNSIGNED, PRIMARY KEY (port))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void insertDefaultPort() {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("INSERT INTO current_port(port) VALUES(?)");
            ps.setInt(1, DEFAULT_PORT);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public int getCurrentPort() {
        int port = DEFAULT_PORT;
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (exists()) {
            try {
                ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM current_port");
                rs = ps.executeQuery();
                if (rs.next()) {
                    port = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            insertDefaultPort();
        }

        return port;
    }

    private boolean exists() {
        boolean isDefaultPortAdded = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM current_port");
            rs = ps.executeQuery();
            if (rs.next()) {
                isDefaultPortAdded = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return isDefaultPortAdded;
    }

    public int updateCurrentPort() {
        int port = getCurrentPort() + 1;
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("UPDATE current_port SET port=" + port);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return port;
    }
}

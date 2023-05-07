package com.playground.sql.managers.spigot;

import com.playground.spigot.PlayerServer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLInviteManager {

    private final PlayerServer plugin;

    public SQLInviteManager(PlayerServer plugin) {
        this.plugin = plugin;
    }

    public void createInvitesTable() {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS invites(id INT NOT NULL AUTO_INCREMENT, uuid VARCHAR(100), server VARCHAR(100), PRIMARY KEY(id))");
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

    public void addPlayer(UUID uuid, UUID server) {
        PreparedStatement ps = null;

        if (!exists(uuid, server)) {
            try {
                ps = plugin.SQL.getConnection().prepareStatement("INSERT INTO invites(uuid, server) VALUES (?, ?)");
                ps.setString(1, uuid.toString());
                ps.setString(2, server.toString());
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
    }

    public boolean exists(UUID uuid, UUID server) {
        boolean isPlayerCreated = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM invites WHERE uuid=? AND server=?");
            ps.setString(1, uuid.toString());
            ps.setString(2, server.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                isPlayerCreated = true;
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

        return isPlayerCreated;
    }

    public void removePlayer(UUID uuid, UUID server) {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("DELETE FROM invites WHERE uuid=? AND server=?");
            ps.setString(1, uuid.toString());
            ps.setString(2, server.toString());
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

    public void deleteInvites(UUID uuid) {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("DELETE FROM invites WHERE server=?");
            ps.setString(1, uuid.toString());
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
}

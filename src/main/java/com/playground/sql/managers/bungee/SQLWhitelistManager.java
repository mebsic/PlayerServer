package com.playground.sql.managers.bungee;

import com.playground.bungee.BungeeManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLWhitelistManager {

    public void createWhitelistTable() {
        PreparedStatement ps = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS whitelist(id INT NOT NULL AUTO_INCREMENT, uuid VARCHAR(100), PRIMARY KEY(id))");
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

    public void addPlayer(UUID uuid) {
        PreparedStatement ps = null;

        if (!exists(uuid)) {
            try {
                ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("INSERT INTO whitelist(uuid) VALUES (?)");
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

    public boolean exists(UUID uuid) {
        boolean isPlayedWhitelisted = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("SELECT * FROM whitelist WHERE uuid=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                isPlayedWhitelisted = true;
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

        return isPlayedWhitelisted;
    }

    public void removePlayer(UUID uuid) {
        PreparedStatement ps = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("DELETE FROM whitelist WHERE uuid=?");
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

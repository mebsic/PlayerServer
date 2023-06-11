package com.playground.sql.managers.bungee;

import com.playground.bungee.BungeeManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLMaintenanceManager {

    public void createMaintenanceTable() {
        PreparedStatement ps = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS maintenance(enabled BOOLEAN, PRIMARY KEY (enabled))");
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

        if (!exists()) {
            setDefaultMaintenanceState();
        }
    }

    public void setDefaultMaintenanceState() {
        PreparedStatement ps = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("INSERT INTO maintenance(enabled) VALUES(?)");
            ps.setBoolean(1, false);
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

    public void setEnabled(boolean enabled) {
        PreparedStatement ps = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("UPDATE maintenance SET enabled=" + enabled);
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

    private boolean exists() {
        boolean isDefaultMaintenanceStateAdded = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("SELECT * FROM maintenance");
            rs = ps.executeQuery();
            if (rs.next()) {
                isDefaultMaintenanceStateAdded = true;
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

        return isDefaultMaintenanceStateAdded;
    }

    public boolean isEnabled() {
        boolean isEnabled = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = BungeeManager.getInstance().getSQL().getConnection().prepareStatement("SELECT * FROM maintenance");
            rs = ps.executeQuery();
            if (rs.next()) {
                isEnabled = rs.getBoolean(1);
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

        return isEnabled;
    }
}

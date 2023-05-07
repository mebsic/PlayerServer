package com.playground.sql.managers.spigot;

import com.playground.spigot.PlayerServer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLServerManager {

    private final PlayerServer plugin;

    public SQLServerManager(PlayerServer plugin) {
        this.plugin = plugin;
    }

    public void createServersTable() {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS servers(uuid VARCHAR(100), port INT UNSIGNED, online BOOLEAN, creating BOOLEAN, PRIMARY KEY (uuid))");
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

    public void createServer(UUID uuid, int port, boolean online, boolean creating) {
        PreparedStatement ps = null;

        if (!exists(uuid)) {
            try {
                ps = plugin.SQL.getConnection().prepareStatement("INSERT INTO servers(uuid, port, online, creating) VALUES (?, ?, ?, ?)");
                ps.setString(1, uuid.toString());
                ps.setInt(2, port);
                ps.setBoolean(3, online);
                ps.setBoolean(4, creating);
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
        boolean isServerCreated = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM servers WHERE uuid=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                isServerCreated = true;
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

        return isServerCreated;
    }

    public int getPort(UUID uuid) {
        int port = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM servers WHERE uuid=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                port = rs.getInt(2);
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

        return port;
    }

    public void remove(UUID uuid) {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("DELETE FROM servers WHERE uuid=?");
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

    public boolean isOnline(UUID uuid, int port) {
        boolean isServerOnline = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM servers WHERE uuid=? AND port=?");
            ps.setString(1, uuid.toString());
            ps.setInt(2, port);
            rs = ps.executeQuery();
            if (rs.next()) {
                isServerOnline = rs.getBoolean(3);
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

        return isServerOnline;
    }

    public void setOnline(UUID uuid, boolean online) {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("UPDATE servers SET online=? WHERE uuid=?");
            ps.setBoolean(1, online);
            ps.setString(2, uuid.toString());
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

    public boolean isCreating(UUID uuid) {
        boolean isCreatingServer = false;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("SELECT * FROM servers WHERE uuid=?");
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                isCreatingServer = rs.getBoolean(4);
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

        return isCreatingServer;
    }

    public void setCreating(UUID uuid, boolean creating) {
        PreparedStatement ps = null;

        try {
            ps = plugin.SQL.getConnection().prepareStatement("UPDATE servers SET creating=? WHERE uuid=?");
            ps.setBoolean(1, creating);
            ps.setString(2, uuid.toString());
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

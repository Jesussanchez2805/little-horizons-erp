package com.littlehorizons.erp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager for Little Horizons ERP.
 * Manages configuration loading, connection lifecycle, and thread-safe access
 * to the MySQL database.
 * 
 * Design Pattern: Singleton Pattern.
 * Guarantees a single centralized access point for database connectivity.
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public final class DatabaseConnection {

    private static DatabaseConnection instance;

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private String params;

    private DatabaseConnection() {
        loadConfiguration();
    }

    /**
     * Retrieves the single global instance of the DatabaseConnection manager.
     *
     * @return Single DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Loads database credentials and parameters from classpath properties file 'db.properties'.
     * Falls back to defaults if properties file is unavailable.
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
                this.host = props.getProperty("db.host", "localhost");
                this.port = Integer.parseInt(props.getProperty("db.port", "3306"));
                this.database = props.getProperty("db.name", "erp_little_horizons");
                this.user = props.getProperty("db.user", "root");
                this.password = props.getProperty("db.password", "root");
                this.params = props.getProperty("db.params", "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota&characterEncoding=UTF-8");
            } else {
                setDefaults();
            }
        } catch (IOException | NumberFormatException e) {
            AppLogger.warning("Could not read 'db.properties', applying standard fallback settings: " + e.getMessage());
            setDefaults();
        }
    }

    private void setDefaults() {
        this.host = "localhost";
        this.port = 3306;
        this.database = "erp_little_horizons";
        this.user = "root";
        this.password = "root";
        this.params = "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota&characterEncoding=UTF-8";
    }

    /**
     * Constructs the full JDBC connection URL.
     *
     * @return JDBC URL string
     */
    public String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s%s", host, port, database, params);
    }

    /**
     * Obtains an active connection to the MySQL database.
     *
     * @return Newly opened java.sql.Connection
     * @throws SQLException If database access error occurs
     */
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(getJdbcUrl(), user, password);
        } catch (SQLException e) {
            AppLogger.severe("Failed to obtain MySQL database connection to " + getJdbcUrl(), e);
            throw e;
        }
    }

    /**
     * Tests whether the database server is reachable with the current credentials.
     *
     * @return true if connection succeeds, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Dynamic setters allowing the user to configure custom credentials at runtime
    public void setCredentials(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
        AppLogger.info("Database credentials updated dynamically for user: " + user);
    }

    public String getUser() { return user; }
    public String getDatabase() { return database; }
    public String getHost() { return host; }
    public int getPort() { return port; }
}

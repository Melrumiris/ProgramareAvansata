package gov.Lab6.conn;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnManager {
    private static DBConnManager instance;
    public static DBConnManager getInstance() {
        if (instance == null) instance = new DBConnManager();
        return instance;
    }

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    private final HikariDataSource ds;
    private Connection conn;

    private DBConnManager() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
            System.err.println("Database connection failed:" + e.getMessage());
            e.printStackTrace();
        }
    }
    public void close() {
        try{
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        }catch (SQLException e) {
            System.err.println("Failed to close database connection:" + e.getMessage());
            e.printStackTrace();
        }
    }
    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = ds.getConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection unavailable: " + e.getMessage(), e);
        }
        return conn;
    }
}

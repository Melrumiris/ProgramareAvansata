package gov.Lab6.conn;

import org.postgresql.core.JdbcCallParseInfo;

import java.sql.Connection;
import java.sql.DriverManager;
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

    private Connection conn;

    private DBConnManager() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
    public Connection getConnection(){
        return conn;
    }
}

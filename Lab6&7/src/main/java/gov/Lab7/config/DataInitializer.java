package gov.Lab7.config;

import gov.Lab6.conn.DBConnManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Connection conn = DBConnManager.getInstance().getConnection();
        try {
            var check = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?");
            check.setString(1, "admin");
            var rs = check.executeQuery();
            if (!rs.next()) {
                var insert = conn.prepareStatement(
                        "INSERT INTO users (username, password, enabled) VALUES (?, ?, TRUE)");
                insert.setString(1, "admin");
                insert.setString(2, passwordEncoder.encode("admin123"));
                insert.executeUpdate();
                System.out.println("Default admin user created.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize default user: " + e.getMessage());
        }
    }
}

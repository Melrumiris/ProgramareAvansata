package gov.Lab7.security;

import gov.Lab6.conn.DBConnManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public class JdbcUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Connection conn = DBConnManager.getInstance().getConnection();
        try {
            var stmt = conn.prepareStatement(
                    "SELECT password FROM users WHERE username = ? AND enabled = TRUE");
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return User.withUsername(username)
                        .password(rs.getString("password"))
                        .roles("USER")
                        .build();
            }
        } catch (SQLException e) {
            throw new UsernameNotFoundException("Database error while loading user: " + username, e);
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}

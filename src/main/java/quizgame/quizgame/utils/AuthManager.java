package quizgame.quizgame.utils;

import quizgame.quizgame.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class AuthManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        if (currentUser == null) {
            return new User(
                1,
                "Test User",
                "test@example.com",
                "1234567890",
                "password123",
                1,
                "user",
                new Timestamp(System.currentTimeMillis())
            );
        }
        return currentUser;
    }

    public static User getUserFromDatabase(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("password"),
                    rs.getInt("isEmailVerified"),
                    rs.getString("role"),
                    rs.getTimestamp("created_at")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

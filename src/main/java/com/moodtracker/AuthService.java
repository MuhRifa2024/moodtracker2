package com.moodtracker;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class AuthService {
    public static boolean login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username dan password tidak boleh kosong");
        }

        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return verifyPassword(password, hashedPassword);
            }
            return false; // Username tidak ditemukan
        } catch (SQLException e) {
            System.err.println("Error saat login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean register(String username, String password, String gender) {
        try (Connection conn = DatabaseHelper.getConnection()) {
            String sql = "INSERT INTO users (username, password, gender) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, gender);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
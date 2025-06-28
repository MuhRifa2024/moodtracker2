package com.moodtracker;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    public static void initDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Tabel users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "gender TEXT)");

            // Tabel moods
            stmt.execute("CREATE TABLE IF NOT EXISTS moods (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user TEXT," +
                    "mood TEXT," +
                    "note TEXT," +
                    "date TEXT)");
        } catch (SQLException e) {
            System.err.println("Error saat menginisialisasi database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        // Gunakan path absolut ke root project
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/mood_tracker.db";
        return DriverManager.getConnection(url);
    }

    public static void insertMood(String user, String mood, String note, String date) {
        String sql = "INSERT INTO moods (user, mood, note, date) VALUES (?, ?, ?, ?)";

        System.out.println("Query INSERT: " + sql);
        System.out.println("Parameter: user=" + user + ", mood=" + mood + ", note=" + note + ", date=" + date);

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, mood);
            stmt.setString(3, note);
            stmt.setString(4, date);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saat menyimpan mood: " + e.getMessage());
        }
    }

    public static java.util.List<String[]> getMoodHistory(String username) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT mood, note, date FROM moods WHERE user = ? ORDER BY date DESC";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("mood"),
                        rs.getString("note"),
                        rs.getString("date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static java.util.List<String[]> getMoodHistoryByDate(String username, String tanggal) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT mood, catatan, tanggal FROM moods WHERE user = ? AND tanggal LIKE ? ORDER BY tanggal DESC";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, tanggal + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new String[] {
                        rs.getString("mood"),
                        rs.getString("catatan"),
                        rs.getString("tanggal")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean registerUser(String username, String password, String gender) {
        String sql = "INSERT INTO users (username, password, gender) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, gender);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getGenderByUsername(String username) {
        String sql = "SELECT gender FROM users WHERE username = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("gender");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Laki-laki"; // default
    }
}
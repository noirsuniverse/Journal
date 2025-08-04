package com.yourname.journalapp;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    // Database will be stored in user's application data directory
    private static final String APP_DATA_DIR = System.getProperty("user.home") + "/.lunalog/";
    private static final String DB_PATH = APP_DATA_DIR + "Journal.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    

    static void initializeDatabase() throws SQLException {
        try {
            // 1. Ensure application directory exists
            File dataDir = new File(APP_DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("Created application data directory: " + APP_DATA_DIR);
            }

            // 2. Load SQLite driver
            Class.forName("org.sqlite.JDBC");

            // 3. Initialize database structure
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS journals (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        entry TEXT NOT NULL,
                        image_path TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
                
                System.out.println("Database initialized at: " + DB_PATH);
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void saveJournalEntry(String title, String content, String imagePath) {
        String sql = "INSERT INTO journals (title, entry, image_path) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, imagePath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving journal entry: " + e.getMessage());
            throw new RuntimeException("Failed to save journal entry", e);
        }
    }

    public static List<String> getJournalEntries() {
        List<String> entries = new ArrayList<>();
        String sql = "SELECT title, entry, created_at FROM journals ORDER BY created_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                entries.add(String.format(
                    "Title: %s\nDate: %s\n\n%s",
                    rs.getString("title"),
                    rs.getString("created_at"),
                    rs.getString("entry")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading journal entries: " + e.getMessage());
            throw new RuntimeException("Failed to load journal entries", e);
        }
        
        return entries;
    }
}
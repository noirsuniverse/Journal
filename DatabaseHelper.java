import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DATABASE_URL = "jdbc:sqlite:journal.db";

    // Get a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC"); // Explicitly load the driver
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(DATABASE_URL);
    }

    // Initialize the database (create tables if they don't exist)
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("Initializing database at: " + DATABASE_URL);

            // Create users table
            String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    last_login TEXT,
                    emoji_data TEXT
                );
            """;

            // Create journals table
            String journalsTable = """
                CREATE TABLE IF NOT EXISTS journals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    title TEXT NOT NULL,
                    entry TEXT NOT NULL,
                    image_path TEXT,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """;

            stmt.execute(usersTable);
            stmt.execute(journalsTable);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void saveJournalEntry(int userId, String title, String content, String imagePath) {
        String sql = "INSERT INTO journals(user_id, title, entry, image_path, timestamp) VALUES(?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
    
            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, content);
            pstmt.setString(4, imagePath); // Save the image path
            pstmt.setString(5, new Timestamp(System.currentTimeMillis()).toString());
            pstmt.executeUpdate();
    
            System.out.println("Journal entry saved: " + title);
        } catch (SQLException e) {
            System.err.println("Error saving journal entry: " + e.getMessage());
        }
    }
    
    // Retrieve all journal entries for a user
    public static List<String> getJournalEntries(int userId) {
        List<String> entries = new ArrayList<>();
        String sql = "SELECT title, entry, timestamp FROM journals WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId); // Ensure userId matches the one used in save
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String entry = "Title: " + rs.getString("title") +
                               "\nDate: " + rs.getString("timestamp") +
                               "\n\n" + rs.getString("entry");
                entries.add(entry);
            }
            System.out.println("Retrieved " + entries.size() + " entries for user " + userId);
        } catch (SQLException e) {
            System.err.println("Error fetching journal entries: " + e.getMessage());
        }

        return entries;
    }

    // Check if the database connection is functional
    public static boolean isDatabaseConnected() {
        try (Connection conn = getConnection()) {
            return conn != null;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Testing database initialization...");
            initializeDatabase(); // Call the method to create the database and tables
            System.out.println("Database and tables created successfully!");
        } catch (Exception e) {
            System.err.println("Error during initialization: " + e.getMessage());
        }
    }
    
}



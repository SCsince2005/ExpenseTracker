package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection — Singleton utility class for managing the SQLite database connection.
 *
 * DESIGN PATTERN: Singleton
 * WHY Singleton?
 * - A database connection is an expensive resource. Opening multiple connections wastes memory.
 * - Singleton ensures only ONE connection instance exists throughout the application.
 * - All DAOs share the same connection, which is efficient for a single-user CLI app.
 *
 * HOW it works:
 * 1. The constructor is PRIVATE — no one can call 'new DBConnection()' from outside.
 * 2. A static 'instance' variable holds the single connection.
 * 3. getConnection() is the only way to get the connection — it creates it lazily on first call.
 *
 * JDBC Concepts:
 * - DriverManager.getConnection(url) — creates a connection to the database.
 * - The URL "jdbc:sqlite:expensetracker.db" tells JDBC to use the SQLite driver
 *   and store data in a file called 'expensetracker.db' in the project root.
 *
 * Viva Tip: "JDBC (Java Database Connectivity) is an API that provides a standard way
 *            to interact with any relational database using Java."
 */
public class DBConnection {

    // JDBC connection URL — SQLite stores the entire DB in a single file
    private static final String URL = "jdbc:sqlite:expensetracker.db";

    // The single shared connection instance (Singleton)
    private static Connection connection = null;

    /**
     * PRIVATE constructor — prevents instantiation from outside.
     * This is the key to the Singleton pattern.
     */
    private DBConnection() {
        // Cannot be instantiated
    }

    /**
     * Returns the single shared database connection.
     * Creates it on first call (lazy initialization).
     *
     * WHY synchronized?
     * - If two threads call getConnection() at the exact same time and connection is null,
     *   both might create a new connection. 'synchronized' prevents this race condition.
     * - This ties into the Concurrency topic from the syllabus.
     *
     * @return the shared Connection instance
     * @throws SQLException if the connection cannot be established
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Explicitly load the SQLite JDBC driver class.
                // WHY? DriverManager needs the driver to be registered before it can create
                // a connection. Class.forName() loads the class into memory, which triggers
                // its static initializer that registers the driver with DriverManager.
                // Some newer JDKs do this automatically via ServiceLoader, but explicitly
                // loading it is more reliable and is the classic JDBC pattern.
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found. Make sure sqlite-jdbc.jar is in the classpath.", e);
            }
            connection = DriverManager.getConnection(URL);
            System.out.println("[DBConnection] Connected to SQLite database successfully.");
        }
        return connection;
    }

    /**
     * Initializes the database tables if they don't already exist.
     * Called once at application startup.
     *
     * WHY 'IF NOT EXISTS'?
     * - So running the app multiple times doesn't crash with "table already exists" errors.
     * - This is idempotent — safe to call repeatedly.
     *
     * SQL Concepts:
     * - INTEGER PRIMARY KEY AUTOINCREMENT — auto-generates unique IDs.
     * - REAL — SQLite's floating-point type for storing amounts.
     * - TEXT — SQLite's string type.
     *
     * @throws SQLException if table creation fails
     */
    public static void initializeDatabase() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "monthly_budget REAL NOT NULL DEFAULT 0" +
                ")";

        String createExpensesTable = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "category TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "date TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";

        // try-with-resources: Statement is AutoCloseable, so it's automatically closed
        // even if an exception occurs. This prevents resource leaks.
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createExpensesTable);
            System.out.println("[DBConnection] Database tables initialized.");
        }
    }

    /**
     * Closes the shared database connection.
     * Should be called when the application shuts down.
     *
     * WHY close?
     * - Open connections consume system resources (file handles, memory).
     * - Not closing them can lead to database locks and data corruption.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DBConnection] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
            }
        }
    }
}

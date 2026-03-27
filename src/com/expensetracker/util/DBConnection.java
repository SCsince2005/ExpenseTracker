package com.expensetracker.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Singleton class - manages a single shared SQLite connection
public class DBConnection {

    private static final String URL = "jdbc:sqlite:expensetracker.db";
    private static Connection connection = null;

    private DBConnection() {}

    // synchronized so multiple threads don't create duplicate connections
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found. Check classpath.", e);
            }
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    // creates tables if they don't already exist (safe to call multiple times)
    public static void initializeDatabase() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "monthly_budget REAL NOT NULL DEFAULT 0)";

        String createExpensesTable = "CREATE TABLE IF NOT EXISTS expenses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "category TEXT NOT NULL, "
                + "amount REAL NOT NULL, "
                + "date TEXT NOT NULL, "
                + "FOREIGN KEY (user_id) REFERENCES users(id))";

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createExpensesTable);
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing DB connection: " + e.getMessage());
            }
        }
    }
}

package com.expensetracker.main;

import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.util.DBConnection;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Main class — Entry point of the Expense Tracker application.
 *
 * Day 1: This is a simple test to verify that:
 * 1. All model classes compile and work correctly.
 * 2. The database connection can be established.
 * 3. Tables are created successfully.
 *
 * From Day 6 onwards, this will become the full CLI menu interface.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   Expense Tracker with Budget Alerts");
        System.out.println("   ---- Day 1: Setup Verification ----");
        System.out.println("==========================================\n");

        // ---- TEST 1: Model classes ----
        System.out.println("--- Testing Model Classes ---\n");

        // Create a User object (no DB yet, just in-memory)
        User user = new User("Rahul", 5000.00);
        System.out.println("Created user: " + user);
        // Notice: toString() is called automatically when you concatenate with +

        // Test the Category enum
        System.out.println("\nAvailable expense categories:");
        for (Category cat : Category.values()) {
            // Category.values() returns an array of ALL enum constants — a built-in method
            System.out.println("  - " + cat.name() + " → " + cat.getDisplayName());
        }

        // Create an Expense object
        Expense expense = new Expense(1, Category.FOOD, 150.50, LocalDate.now());
        System.out.println("\nCreated expense: " + expense);

        // Test validation — this should throw IllegalArgumentException
        System.out.println("\nTesting validation (setting negative budget)...");
        try {
            user.setMonthlyBudget(-1000);
            System.out.println("ERROR: Should not reach here!");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation works! Caught: " + e.getMessage());
        }

        // ---- TEST 2: Database Connection ----
        System.out.println("\n--- Testing Database Connection ---\n");

        try {
            // Initialize the database (creates tables if they don't exist)
            DBConnection.initializeDatabase();
            System.out.println("Database setup complete!");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Always close the connection when done — good resource management
            DBConnection.closeConnection();
        }

        System.out.println("\n==========================================");
        System.out.println("   Day 1 setup verified successfully!");
        System.out.println("==========================================");
    }
}

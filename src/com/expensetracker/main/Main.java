package com.expensetracker.main;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.dao.UserDAO;
import com.expensetracker.exception.ExpenseTrackerException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;
import com.expensetracker.util.DBConnection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   Expense Tracker with Budget Alerts");
        System.out.println("   ---- Day 3: DAO Layer Test ----");
        System.out.println("==========================================\n");

        try {
            // setup database tables
            DBConnection.initializeDatabase();
            System.out.println("Database initialized.\n");

            // wire up layers: DAO -> Service
            ExpenseDAO expenseDAO = new ExpenseDAO();
            UserDAO userDAO = new UserDAO();
            ExpenseService expenseService = new ExpenseService(expenseDAO);
            UserService userService = new UserService(userDAO, expenseService);

            // --- register a user (saved to DB) ---
            System.out.println("--- Registering User ---");
            User rahul = userService.registerUser("Rahul", 5000.00);
            System.out.println("Registered: " + rahul);

            // --- add expenses (saved to DB) ---
            System.out.println("\n--- Adding Expenses ---");
            expenseService.addExpense(new Expense(rahul.getId(), Category.FOOD, 250.00, LocalDate.now()));
            expenseService.addExpense(new Expense(rahul.getId(), Category.FOOD, 180.00, LocalDate.now().minusDays(1)));
            expenseService.addExpense(new Expense(rahul.getId(), Category.TRANSPORT, 100.00, LocalDate.now()));
            expenseService.addExpense(new Expense(rahul.getId(), Category.STUDY, 450.00, LocalDate.now().minusDays(2)));
            expenseService.addExpense(new Expense(rahul.getId(), Category.ENTERTAINMENT, 300.00, LocalDate.now()));
            expenseService.addExpense(new Expense(rahul.getId(), Category.RENT, 2000.00, LocalDate.now().minusDays(5)));
            System.out.println("6 expenses added.");

            // --- list all expenses for user ---
            System.out.println("\n--- All Expenses ---");
            List<Expense> allExpenses = expenseService.getExpensesByUser(rahul.getId());
            for (Expense e : allExpenses) {
                System.out.println("  " + e);
            }

            // --- total spending ---
            double total = expenseService.getTotalByUser(rahul.getId());
            System.out.println("\nTotal spent: Rs." + String.format("%.2f", total));

            // --- category-wise breakdown ---
            System.out.println("\n--- Category Breakdown ---");
            Map<Category, Double> catTotals = expenseService.getCategoryWiseTotals(rahul.getId());
            for (Map.Entry<Category, Double> entry : catTotals.entrySet()) {
                System.out.printf("  %-25s : Rs.%.2f%n", entry.getKey().getDisplayName(), entry.getValue());
            }

            // --- remaining budget ---
            double remaining = userService.getRemainingBudget(rahul.getId());
            System.out.printf("%nBudget: Rs.%.2f | Spent: Rs.%.2f | Remaining: Rs.%.2f%n",
                    rahul.getMonthlyBudget(), total, remaining);

            if (remaining < 0) {
                System.out.println("WARNING: Budget exceeded by Rs." + String.format("%.2f", Math.abs(remaining)));
            } else if (remaining < rahul.getMonthlyBudget() * 0.2) {
                System.out.println("ALERT: Less than 20% budget remaining!");
            }

            // --- verify data persists by re-reading from DB ---
            System.out.println("\n--- Verify: Re-read user from DB ---");
            User fromDb = userService.getUserById(rahul.getId())
                    .orElseThrow(() -> new ExpenseTrackerException("User not found in DB!"));
            System.out.println("From DB: " + fromDb);

            // --- validation tests ---
            System.out.println("\n--- Validation Tests ---");

            try {
                expenseService.addExpense(null);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught: " + e.getMessage());
            }

            try {
                userService.registerUser("", 3000.00);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught: " + e.getMessage());
            }

            try {
                userService.updateBudget(rahul.getId(), -500);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (ExpenseTrackerException e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection();
        }

        System.out.println("\n==========================================");
        System.out.println("   Day 3 DAO layer test complete!");
        System.out.println("==========================================");
    }
}

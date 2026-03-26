package com.expensetracker.main;

import com.expensetracker.exception.ExpenseTrackerException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Main class -- Entry point of the Expense Tracker application.
 *
 * Day 2: Tests the service layer (ExpenseService + UserService) with in-memory data.
 * This validates that business logic works correctly before wiring to the database on Day 3.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   Expense Tracker with Budget Alerts");
        System.out.println("   ---- Day 2: Service Layer Test ----");
        System.out.println("==========================================\n");

        // Create services -- notice ExpenseService is injected into UserService
        // This is DEPENDENCY INJECTION: UserService needs ExpenseService to calculate remaining budget
        ExpenseService expenseService = new ExpenseService();
        UserService userService = new UserService(expenseService);

        try {
            // ---- TEST 1: Register a user ----
            System.out.println("--- Test 1: Register User ---\n");
            User rahul = userService.registerUser("Rahul", 5000.00);
            System.out.println("Registered: " + rahul);

            // ---- TEST 2: Add expenses ----
            System.out.println("\n--- Test 2: Add Expenses ---\n");

            expenseService.addExpense(new Expense(rahul.getId(), Category.FOOD, 250.00, LocalDate.now()));
            expenseService.addExpense(new Expense(rahul.getId(), Category.FOOD, 180.00, LocalDate.now().minusDays(1)));
            expenseService.addExpense(new Expense(rahul.getId(), Category.TRANSPORT, 100.00, LocalDate.now()));
            expenseService.addExpense(new Expense(rahul.getId(), Category.STUDY, 450.00, LocalDate.now().minusDays(2)));
            expenseService.addExpense(new Expense(rahul.getId(), Category.ENTERTAINMENT, 300.00, LocalDate.now()));
            expenseService.addExpense(new Expense(rahul.getId(), Category.RENT, 2000.00, LocalDate.now().minusDays(5)));

            System.out.println("\nTotal expenses added: " + expenseService.getTotalExpenseCount());

            // ---- TEST 3: View all expenses for the user ----
            System.out.println("\n--- Test 3: All Expenses for " + rahul.getName() + " ---\n");
            List<Expense> rahulExpenses = expenseService.getExpensesByUser(rahul.getId());
            for (Expense e : rahulExpenses) {
                System.out.println("  " + e);
            }

            // ---- TEST 4: Total spending ----
            System.out.println("\n--- Test 4: Total Spending ---\n");
            double total = expenseService.getTotalByUser(rahul.getId());
            System.out.println("Total spent: Rs." + String.format("%.2f", total));

            // ---- TEST 5: Category-wise breakdown (HashMap) ----
            System.out.println("\n--- Test 5: Category-wise Breakdown ---\n");
            Map<Category, Double> categoryTotals = expenseService.getCategoryWiseTotals(rahul.getId());
            for (Map.Entry<Category, Double> entry : categoryTotals.entrySet()) {
                System.out.printf("  %-25s : Rs.%.2f%n", entry.getKey().getDisplayName(), entry.getValue());
            }

            // ---- TEST 6: Filter by category ----
            System.out.println("\n--- Test 6: Food Expenses Only ---\n");
            List<Expense> foodExpenses = expenseService.getExpensesByCategory(rahul.getId(), Category.FOOD);
            for (Expense e : foodExpenses) {
                System.out.println("  " + e);
            }

            // ---- TEST 7: Remaining budget ----
            System.out.println("\n--- Test 7: Remaining Budget ---\n");
            double remaining = userService.getRemainingBudget(rahul.getId());
            System.out.printf("Budget: Rs.%.2f | Spent: Rs.%.2f | Remaining: Rs.%.2f%n",
                    rahul.getMonthlyBudget(), total, remaining);

            if (remaining < 0) {
                System.out.println("WARNING: You have EXCEEDED your budget by Rs." + String.format("%.2f", Math.abs(remaining)));
            } else if (remaining < rahul.getMonthlyBudget() * 0.2) {
                System.out.println("ALERT: Less than 20% of your budget remains!");
            }

            // ---- TEST 8: Validation testing (should throw exceptions) ----
            System.out.println("\n--- Test 8: Validation Tests ---\n");

            // Test: null expense
            try {
                expenseService.addExpense(null);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught (null expense): " + e.getMessage());
            }

            // Test: duplicate user
            try {
                userService.registerUser("Rahul", 3000.00);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught (duplicate user): " + e.getMessage());
            }

            // Test: empty name
            try {
                userService.registerUser("", 3000.00);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught (empty name): " + e.getMessage());
            }

            // Test: negative budget
            try {
                userService.updateBudget(rahul.getId(), -500);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught (negative budget): " + e.getMessage());
            }

            // Test: user not found
            try {
                userService.getRemainingBudget(999);
            } catch (ExpenseTrackerException e) {
                System.out.println("Caught (user not found): " + e.getMessage());
            }

        } catch (ExpenseTrackerException e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n==========================================");
        System.out.println("   Day 2 service layer test complete!");
        System.out.println("==========================================");
    }
}

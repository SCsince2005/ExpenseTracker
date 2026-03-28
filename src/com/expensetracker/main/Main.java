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
        System.out.println("   ---- Day 4: Full Integration ----");
        System.out.println("==========================================\n");

        try {
            DBConnection.initializeDatabase();

            // wire layers
            ExpenseDAO expenseDAO = new ExpenseDAO();
            UserDAO userDAO = new UserDAO();
            ExpenseService expenseService = new ExpenseService(expenseDAO);
            UserService userService = new UserService(userDAO, expenseService);

            // register user (checks for duplicates via DB)
            System.out.println("--- Register User ---");
            User rahul = userService.registerUser("Rahul", 5000.00);
            System.out.println("Registered: " + rahul);

            // add expenses across this month and last month
            System.out.println("\n--- Add Expenses ---");
            LocalDate today = LocalDate.now();

            Expense e1 = new Expense(rahul.getId(), Category.FOOD, 250.00, today);
            Expense e2 = new Expense(rahul.getId(), Category.FOOD, 180.00, today.minusDays(1));
            Expense e3 = new Expense(rahul.getId(), Category.TRANSPORT, 100.00, today);
            Expense e4 = new Expense(rahul.getId(), Category.STUDY, 450.00, today.minusDays(2));
            Expense e5 = new Expense(rahul.getId(), Category.ENTERTAINMENT, 300.00, today);
            Expense e6 = new Expense(rahul.getId(), Category.RENT, 2000.00, today.minusDays(5));

            expenseService.addExpense(e1);
            expenseService.addExpense(e2);
            expenseService.addExpense(e3);
            expenseService.addExpense(e4);
            expenseService.addExpense(e5);
            expenseService.addExpense(e6);
            System.out.println("Added 6 expenses.");

            // all expenses
            System.out.println("\n--- All Expenses ---");
            List<Expense> all = expenseService.getExpensesByUser(rahul.getId());
            for (Expense e : all) {
                System.out.println("  " + e);
            }
            System.out.println("Count: " + all.size());

            // monthly report for current month
            int year = today.getYear();
            int month = today.getMonthValue();
            System.out.println("\n--- Monthly Report (" + year + "-" + String.format("%02d", month) + ") ---");

            Map<Category, Double> monthlyReport = expenseService.getMonthlyReport(rahul.getId(), year, month);
            double monthlyTotal = 0;
            for (Map.Entry<Category, Double> entry : monthlyReport.entrySet()) {
                System.out.printf("  %-25s : Rs.%.2f%n", entry.getKey().getDisplayName(), entry.getValue());
                monthlyTotal += entry.getValue();
            }
            System.out.printf("  %-25s : Rs.%.2f%n", "TOTAL", monthlyTotal);

            // delete an expense and verify count drops
            System.out.println("\n--- Delete Expense (id=" + e3.getId() + ", Transport Rs.100) ---");
            expenseService.deleteExpense(e3.getId());
            List<Expense> afterDelete = expenseService.getExpensesByUser(rahul.getId());
            System.out.println("Count after delete: " + afterDelete.size());

            // updated totals
            double newTotal = expenseService.getTotalByUser(rahul.getId());
            System.out.println("Total after delete: Rs." + String.format("%.2f", newTotal));

            // remaining budget
            System.out.println("\n--- Budget Status ---");
            double remaining = userService.getRemainingBudget(rahul.getId());
            System.out.printf("Budget: Rs.%.2f | Spent: Rs.%.2f | Remaining: Rs.%.2f%n",
                    rahul.getMonthlyBudget(), newTotal, remaining);

            if (remaining < 0) {
                System.out.println("WARNING: Over budget by Rs." + String.format("%.2f", Math.abs(remaining)));
            } else if (remaining < rahul.getMonthlyBudget() * 0.2) {
                System.out.println("ALERT: Less than 20% budget remaining!");
            } else {
                System.out.println("Budget is on track.");
            }

            // duplicate user test
            System.out.println("\n--- Validation ---");
            try {
                userService.registerUser("Rahul", 3000);
            } catch (ExpenseTrackerException ex) {
                System.out.println("Caught duplicate: " + ex.getMessage());
            }

            // delete non-existent expense test
            try {
                expenseService.deleteExpense(9999);
            } catch (ExpenseTrackerException ex) {
                System.out.println("Caught bad delete: " + ex.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
        } catch (ExpenseTrackerException e) {
            System.err.println("App error: " + e.getMessage());
        } finally {
            DBConnection.closeConnection();
        }

        System.out.println("\n==========================================");
        System.out.println("   Day 4 integration test complete!");
        System.out.println("==========================================");
    }
}

package com.expensetracker.service;

import com.expensetracker.exception.ExpenseTrackerException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExpenseService -- Business logic layer for managing expenses.
 *
 * ARCHITECTURE: This is the SERVICE layer in our layered architecture:
 *   Main (CLI) --> Service (business logic) --> DAO (database)
 *
 * WHY a separate service layer?
 * - Separates WHAT to do (business rules) from HOW to store it (database).
 * - If we switch from SQLite to MySQL, only the DAO changes -- service stays the same.
 * - This is the Single Responsibility Principle (SRP) from SOLID.
 *
 * COLLECTIONS USED:
 * 1. ArrayList<Expense> -- Dynamic array that grows automatically. Used to store all expenses.
 *    WHY ArrayList? Fast random access O(1), good for iterating all expenses.
 * 2. HashMap<Category, Double> -- Maps each category to its total spending.
 *    WHY HashMap? Fast key lookup O(1), perfect for category-wise totals.
 * 3. Streams API -- Functional-style operations for filtering and aggregating.
 *
 * DAY 3: The ArrayList will be replaced by database calls through ExpenseDAO.
 *
 * Viva Tip: "ArrayList is backed by a resizable array. It's fast for get/set (O(1))
 *            but slow for insert/delete in the middle (O(n)) because elements shift."
 */
public class ExpenseService {

    /**
     * In-memory storage for expenses.
     * WHY ArrayList and not LinkedList?
     * - We mostly iterate (for totals/reports) and access by index -- ArrayList excels at both.
     * - LinkedList is better only when you frequently insert/delete in the middle.
     *
     * This will be replaced by ExpenseDAO (database) on Day 3.
     */
    private final List<Expense> expenses = new ArrayList<>();

    /**
     * Auto-incrementing ID counter to simulate database AUTOINCREMENT.
     * On Day 3, the database will handle IDs automatically.
     */
    private int nextId = 1;

    /**
     * Adds a new expense after validation.
     *
     * WHY throw ExpenseTrackerException (checked)?
     * - Forces the caller (Main.java) to handle the error with try-catch.
     * - The caller can show a user-friendly message instead of crashing.
     *
     * @param expense the expense to add
     * @throws ExpenseTrackerException if the expense is invalid
     */
    public void addExpense(Expense expense) throws ExpenseTrackerException {
        // Validate the expense before adding
        validateExpense(expense);

        // Assign an auto-incrementing ID (simulates DB AUTOINCREMENT)
        expense.setId(nextId++);

        // Add to the in-memory list
        expenses.add(expense);

        System.out.println("[ExpenseService] Expense added: " + expense);
    }

    /**
     * Validates an expense object.
     *
     * WHY separate validation method?
     * - Single Responsibility: addExpense() handles storage, validateExpense() handles rules.
     * - Can be reused for updateExpense() later without duplicating validation code.
     *
     * @param expense the expense to validate
     * @throws ExpenseTrackerException if any validation rule fails
     */
    private void validateExpense(Expense expense) throws ExpenseTrackerException {
        if (expense == null) {
            throw new ExpenseTrackerException("Expense cannot be null.");
        }
        if (expense.getAmount() <= 0) {
            throw new ExpenseTrackerException("Expense amount must be greater than zero. Got: " + expense.getAmount());
        }
        if (expense.getCategory() == null) {
            throw new ExpenseTrackerException("Expense category is required.");
        }
        if (expense.getDate() == null) {
            throw new ExpenseTrackerException("Expense date is required.");
        }
        if (expense.getUserId() <= 0) {
            throw new ExpenseTrackerException("Expense must be associated with a valid user (userId > 0).");
        }
    }

    /**
     * Returns all expenses for a given user.
     *
     * STREAMS API (Java 8):
     * - .stream() -- converts the list into a Stream (a pipeline for data processing).
     * - .filter(predicate) -- keeps only elements that match the condition.
     * - .collect(Collectors.toList()) -- gathers results back into a List.
     *
     * WHY Streams?
     * - More readable than manual for-loops with if-statements.
     * - Declarative: says WHAT to do, not HOW to do it.
     * - Can be parallelized easily (parallelStream()) for large datasets.
     *
     * Viva Tip: "Streams don't modify the original collection. They create a new
     *            pipeline of operations that produces a result."
     *
     * @param userId the user whose expenses to retrieve
     * @return list of expenses belonging to the user
     */
    public List<Expense> getExpensesByUser(int userId) {
        return expenses.stream()
                .filter(e -> e.getUserId() == userId)   // Lambda: keep only this user's expenses
                .collect(Collectors.toList());           // Collect results into a new ArrayList
    }

    /**
     * Calculates the total spending for a user.
     *
     * STREAMS:
     * - .mapToDouble(Expense::getAmount) -- extracts the amount from each Expense.
     *   This is a METHOD REFERENCE (shorthand for e -> e.getAmount()).
     * - .sum() -- terminal operation that adds up all the doubles.
     *
     * Viva Tip: "Method references (Class::method) are shorthand for lambdas.
     *            Expense::getAmount is equivalent to e -> e.getAmount()."
     *
     * @param userId the user whose total to calculate
     * @return total amount spent
     */
    public double getTotalByUser(int userId) {
        return expenses.stream()
                .filter(e -> e.getUserId() == userId)
                .mapToDouble(Expense::getAmount)    // Method reference
                .sum();                              // Terminal operation
    }

    /**
     * Filters expenses by user AND category.
     *
     * Demonstrates CHAINING multiple .filter() calls -- each one narrows the results.
     *
     * @param userId   the user
     * @param category the category to filter by
     * @return list of matching expenses
     */
    public List<Expense> getExpensesByCategory(int userId, Category category) {
        return expenses.stream()
                .filter(e -> e.getUserId() == userId)
                .filter(e -> e.getCategory() == category)  // Enum comparison with == is safe
                .collect(Collectors.toList());
    }

    /**
     * Returns a category-wise breakdown of spending for a user.
     *
     * HASHMAP<Category, Double>:
     * - Key: the Category enum constant (e.g., FOOD, TRANSPORT)
     * - Value: total amount spent in that category
     *
     * WHY HashMap?
     * - O(1) average time for put/get operations.
     * - Perfect for key-value mapping (category -> total).
     *
     * ALGORITHM:
     * 1. Create an empty HashMap.
     * 2. Loop through all expenses for this user.
     * 3. For each expense, use getOrDefault() to get current total (default 0.0),
     *    add the new amount, and put it back.
     *
     * merge() is a modern alternative:
     *   map.merge(key, value, Double::sum) -- if key exists, sum old + new; else put new.
     *
     * Viva Tip: "HashMap uses hashing -- it computes a hash of the key to find the bucket,
     *            then uses equals() to find the exact entry. This gives O(1) average lookup."
     *
     * @param userId the user
     * @return map of category to total amount
     */
    public Map<Category, Double> getCategoryWiseTotals(int userId) {
        Map<Category, Double> categoryTotals = new HashMap<>();

        for (Expense expense : getExpensesByUser(userId)) {
            // merge(): if the key exists, apply the merge function (sum); else insert the value.
            // Double::sum is a method reference for (a, b) -> a + b
            categoryTotals.merge(
                    expense.getCategory(),    // Key
                    expense.getAmount(),      // Value to insert if absent
                    Double::sum               // Merge function if key exists
            );
        }

        return categoryTotals;
    }

    /**
     * Returns the total number of expenses stored (all users).
     * Useful for testing and reporting.
     */
    public int getTotalExpenseCount() {
        return expenses.size();
    }
}

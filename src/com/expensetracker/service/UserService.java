package com.expensetracker.service;

import com.expensetracker.exception.ExpenseTrackerException;
import com.expensetracker.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserService -- Business logic layer for managing users.
 *
 * ARCHITECTURE:
 *   Main (CLI) --> UserService (business logic) --> UserDAO (database, Day 3)
 *
 * WHY a separate UserService (not just put user logic inside ExpenseService)?
 * - Single Responsibility Principle (SRP): each class has ONE job.
 * - UserService manages USERS, ExpenseService manages EXPENSES.
 * - If user logic changes (e.g., adding email field), only UserService is modified.
 *
 * COLLECTIONS USED:
 * - ArrayList<User> -- stores registered users (in-memory, replaced by DAO on Day 3).
 *
 * JAVA FEATURE: Optional<T>
 * - Introduced in Java 8 to avoid returning null (which causes NullPointerException).
 * - Optional forces the caller to explicitly handle the "not found" case.
 *
 * Viva Tip: "SRP states that a class should have only one reason to change.
 *            UserService changes only when user-related requirements change."
 */
public class UserService {

    /**
     * In-memory storage for users.
     * Will be replaced by UserDAO (JDBC) on Day 3.
     */
    private final List<User> users = new ArrayList<>();

    /**
     * Auto-incrementing ID counter (simulates DB AUTOINCREMENT).
     */
    private int nextId = 1;

    /**
     * Reference to ExpenseService -- needed to calculate remaining budget.
     *
     * WHY is this a constructor dependency (not created inside)?
     * - This is DEPENDENCY INJECTION -- the caller provides the dependency.
     * - Benefits: testable (can pass a mock), flexible (can swap implementations).
     *
     * Viva Tip: "Dependency Injection means a class receives its dependencies from
     *            the outside rather than creating them itself. This reduces coupling."
     */
    private final ExpenseService expenseService;

    public UserService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Registers a new user with a name and monthly budget.
     *
     * @param name          the user's name
     * @param monthlyBudget the monthly budget amount
     * @return the created User object with an assigned ID
     * @throws ExpenseTrackerException if validation fails
     */
    public User registerUser(String name, double monthlyBudget) throws ExpenseTrackerException {
        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new ExpenseTrackerException("User name cannot be null or empty.");
        }
        if (monthlyBudget < 0) {
            throw new ExpenseTrackerException("Monthly budget cannot be negative.");
        }

        // Check for duplicate names (business rule)
        for (User existing : users) {
            if (existing.getName().equalsIgnoreCase(name.trim())) {
                throw new ExpenseTrackerException("User '" + name + "' already exists.");
            }
        }

        // Create the user with an auto-generated ID
        User user = new User(nextId++, name.trim(), monthlyBudget);
        users.add(user);

        System.out.println("[UserService] User registered: " + user);
        return user;
    }

    /**
     * Looks up a user by their ID.
     *
     * OPTIONAL<T> (Java 8):
     * - Instead of returning null when a user is not found, we return Optional.empty().
     * - The caller MUST use isPresent()/get() or orElse() to access the value.
     * - This makes "not found" explicit and prevents NullPointerException.
     *
     * WHY Optional instead of null?
     * - Null is ambiguous: does it mean "not found" or "error" or "not initialized"?
     * - Optional makes the intent clear: "this method might not return a value."
     *
     * Viva Tip: "Optional is a container that may or may not hold a value.
     *            Use it to avoid returning null from methods."
     *
     * @param id the user ID to search for
     * @return Optional containing the user, or Optional.empty() if not found
     */
    public Optional<User> getUserById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst();   // Returns Optional<User> -- either has a value or is empty
    }

    /**
     * Updates the monthly budget for a user.
     *
     * @param userId    the user's ID
     * @param newBudget the new budget amount
     * @throws ExpenseTrackerException if user not found or budget is invalid
     */
    public void updateBudget(int userId, double newBudget) throws ExpenseTrackerException {
        if (newBudget < 0) {
            throw new ExpenseTrackerException("Budget cannot be negative.");
        }

        // Use Optional -- if user not found, throw exception
        User user = getUserById(userId)
                .orElseThrow(() -> new ExpenseTrackerException("User with ID " + userId + " not found."));

        user.setMonthlyBudget(newBudget);
        System.out.println("[UserService] Budget updated: " + user);
    }

    /**
     * Calculates the remaining budget for a user.
     * Remaining = Monthly Budget - Total Expenses
     *
     * @param userId the user's ID
     * @return remaining budget (can be negative if overspent!)
     * @throws ExpenseTrackerException if user not found
     */
    public double getRemainingBudget(int userId) throws ExpenseTrackerException {
        User user = getUserById(userId)
                .orElseThrow(() -> new ExpenseTrackerException("User with ID " + userId + " not found."));

        double totalSpent = expenseService.getTotalByUser(userId);
        return user.getMonthlyBudget() - totalSpent;
    }

    /**
     * Returns all registered users.
     *
     * WHY return a new ArrayList instead of the internal one?
     * - DEFENSIVE COPY: prevents external code from modifying our internal list.
     * - If we returned 'users' directly, the caller could add/remove users
     *   without going through our validation logic, breaking encapsulation.
     *
     * Viva Tip: "Returning a defensive copy of internal collections protects
     *            the object's state from unauthorized modification."
     *
     * @return a copy of the users list
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);  // Defensive copy
    }
}

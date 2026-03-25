package com.expensetracker.exception;

/**
 * Custom checked exception for the Expense Tracker application.
 *
 * WHY a custom exception?
 * 1. Generic exceptions (Exception, RuntimeException) don't tell you WHAT went wrong
 *    in your application. A custom exception makes error handling specific and meaningful.
 * 2. This is a CHECKED exception (extends Exception, not RuntimeException).
 *    WHY checked? Because expense/database errors are RECOVERABLE — the program can
 *    catch them and show a user-friendly message instead of crashing.
 * 3. The second constructor takes a 'Throwable cause' — this is called EXCEPTION CHAINING.
 *    WHY? When a SQLException occurs in the DAO layer, we wrap it in our custom exception
 *    so the service layer doesn't need to know about SQL details, but the original error
 *    is preserved for debugging.
 *
 * Exception Hierarchy:
 *   Throwable
 *     +-- Error (serious, don't catch -- e.g., OutOfMemoryError)
 *     +-- Exception (recoverable)
 *           +-- IOException, SQLException, ... (checked)
 *           +-- ExpenseTrackerException        <-- OUR CUSTOM EXCEPTION (checked)
 *           +-- RuntimeException (unchecked)
 *                 +-- IllegalArgumentException, NullPointerException, ...
 *
 * Viva Tip: "Checked exceptions force the caller to handle them (try-catch or throws),
 *            making error handling explicit. Unchecked exceptions indicate programming bugs."
 */
public class ExpenseTrackerException extends Exception {

    /**
     * Constructor with just a message.
     * Usage: throw new ExpenseTrackerException("User not found");
     */
    public ExpenseTrackerException(String message) {
        super(message);
    }

    /**
     * Constructor with message AND the original cause (exception chaining).
     * Usage: catch (SQLException e) {
     *            throw new ExpenseTrackerException("Failed to save expense", e);
     *        }
     * WHY? The caller sees our custom message, but e.getCause() still has the original SQL error.
     */
    public ExpenseTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}

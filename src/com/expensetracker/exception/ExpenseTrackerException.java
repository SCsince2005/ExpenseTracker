package com.expensetracker.exception;

// Custom checked exception for handling application-specific errors
public class ExpenseTrackerException extends Exception {

    public ExpenseTrackerException(String message) {
        super(message);
    }

    // wraps original cause so we don't lose the root error info
    public ExpenseTrackerException(String message, Throwable cause) {
        super(message, cause);
    }
}

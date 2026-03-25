package com.expensetracker.model;

import java.time.LocalDate;

/**
 * Expense model class — maps to the 'expenses' table in the database.
 *
 * OOP Concepts demonstrated:
 * 1. ENCAPSULATION — private fields with controlled access.
 * 2. COMPOSITION — Expense HAS-A Category (uses the Category enum as a field).
 *    WHY? This is better than using a raw String because the compiler ensures
 *    you can only use valid categories.
 * 3. Use of java.time.LocalDate — the modern, immutable date class (Java 8+).
 *    WHY not java.util.Date? Date is mutable and poorly designed. LocalDate is thread-safe.
 *
 * Viva Tip: "Composition (HAS-A) is preferred over inheritance (IS-A) when classes
 *            don't share a parent-child relationship. Expense HAS a Category."
 */
public class Expense {

    private int id;
    private int userId;
    private Category category;   // Composition: Expense HAS-A Category
    private double amount;
    private LocalDate date;

    /**
     * Constructor for creating a NEW expense (before saving to DB).
     */
    public Expense(int userId, Category category, double amount, LocalDate date) {
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    /**
     * Constructor for loading an EXISTING expense from the database.
     */
    public Expense(int id, int userId, Category category, double amount, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    // ======================== GETTERS & SETTERS ========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    /**
     * Amount must be positive — you can't spend a negative amount!
     */
    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive.");
        }
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * toString() for readable expense output.
     * String.format("%.2f", amount) formats the amount to 2 decimal places.
     */
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", userId=" + userId +
                ", category=" + category.name() +  // .name() gives the enum constant name (e.g., "FOOD")
                ", amount=₹" + String.format("%.2f", amount) +
                ", date=" + date +
                '}';
    }
}

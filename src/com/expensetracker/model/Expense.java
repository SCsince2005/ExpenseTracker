package com.expensetracker.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Expense model class -- maps to the 'expenses' table in the database.
 *
 * OOP Concepts demonstrated:
 * 1. ENCAPSULATION -- private fields with controlled access.
 * 2. COMPOSITION -- Expense HAS-A Category (uses the Category enum as a field).
 * 3. Use of java.time.LocalDate -- the modern, immutable date class (Java 8+).
 *
 * DAY 2 IMPROVEMENTS:
 * - Added validation in constructors (amount > 0, category not null, date not null).
 * - Added equals() and hashCode() based on 'id'.
 * - Constructors now call setters to reuse validation logic (DRY principle).
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
     *
     * DAY 2: Now validates all inputs via setters.
     * WHY? An Expense with amount=0, null category, or null date is invalid.
     * By validating in the constructor, the object can never be in a bad state.
     */
    public Expense(int userId, Category category, double amount, LocalDate date) {
        this.userId = userId;
        setCategory(category);   // Reuse setter validation
        setAmount(amount);
        setDate(date);
    }

    /**
     * Constructor for loading an EXISTING expense from the database.
     */
    public Expense(int id, int userId, Category category, double amount, LocalDate date) {
        this.id = id;
        this.userId = userId;
        setCategory(category);
        setAmount(amount);
        setDate(date);
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

    /**
     * DAY 2: Category cannot be null -- an expense MUST belong to a category.
     */
    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Expense category cannot be null.");
        }
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    /**
     * Amount must be positive -- you can't spend a negative or zero amount!
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

    /**
     * DAY 2: Date cannot be null -- every expense must be dated.
     * Also rejects future dates (you can't log an expense that hasn't happened).
     */
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Expense date cannot be null.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Expense date cannot be in the future.");
        }
        this.date = date;
    }

    // ======================== equals, hashCode, toString ========================

    /**
     * DAY 2: Two Expense objects are equal if they have the same id.
     * Same pattern and reasoning as User.equals().
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Expense other = (Expense) obj;
        return this.id == other.id;
    }

    /**
     * DAY 2: hashCode() consistent with equals().
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
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
                ", category=" + category.name() +
                ", amount=Rs." + String.format("%.2f", amount) +
                ", date=" + date +
                '}';
    }
}

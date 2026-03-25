package com.expensetracker.model;

/**
 * User model class — maps to the 'users' table in the database.
 *
 * OOP Concepts demonstrated:
 * 1. ENCAPSULATION — All fields are private; access only through getters/setters.
 *    WHY? So that external code can't set invalid values (e.g., negative budget).
 * 2. Constructor overloading — two constructors for different use cases.
 * 3. toString() override — readable output for debugging and reports.
 *
 * Viva Tip: "Encapsulation hides internal state and requires all interaction
 *            to happen through well-defined methods, protecting data integrity."
 */
public class User {

    private int id;
    private String name;
    private double monthlyBudget;

    /**
     * Constructor for creating a NEW user (before saving to DB — id not yet assigned).
     * WHY two constructors? When inserting a user, we don't know the id yet (DB auto-generates it).
     * When reading FROM the DB, we already have the id.
     */
    public User(String name, double monthlyBudget) {
        this.name = name;
        this.monthlyBudget = monthlyBudget;
    }

    /**
     * Constructor for loading an EXISTING user from the database (id is known).
     */
    public User(int id, String name, double monthlyBudget) {
        this.id = id;
        this.name = name;
        this.monthlyBudget = monthlyBudget;
    }

    // ======================== GETTERS & SETTERS ========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    /**
     * Setter with basic validation — budget must be positive.
     * WHY validate here? This is the whole point of encapsulation: control HOW data is modified.
     */
    public void setMonthlyBudget(double monthlyBudget) {
        if (monthlyBudget < 0) {
            throw new IllegalArgumentException("Monthly budget cannot be negative.");
        }
        this.monthlyBudget = monthlyBudget;
    }

    /**
     * toString() — called automatically when you print a User object.
     * WHY override? The default toString() prints something like "User@3f2a1b" (memory address),
     * which is useless. We want readable output for debugging and CLI display.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monthlyBudget=₹" + String.format("%.2f", monthlyBudget) +
                '}';
    }
}

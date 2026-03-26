package com.expensetracker.model;

import java.util.Objects;

/**
 * User model class -- maps to the 'users' table in the database.
 *
 * OOP Concepts demonstrated:
 * 1. ENCAPSULATION -- All fields are private; access only through getters/setters.
 * 2. Constructor overloading -- two constructors for different use cases.
 * 3. Constructor VALIDATION -- fail fast at object creation, not later.
 * 4. equals() and hashCode() -- needed for Collections (HashMap, HashSet, contains()).
 * 5. toString() override -- readable output for debugging and reports.
 *
 * DAY 2 IMPROVEMENTS:
 * - Added validation in constructors (not just setters).
 * - Added equals() and hashCode() based on 'id'.
 * - Added name validation in setName().
 *
 * Viva Tip: "If you override equals(), you MUST override hashCode() too.
 *            Otherwise, HashMap and HashSet will not work correctly because
 *            they use hashCode() to determine the bucket, then equals() to compare."
 */
public class User {

    private int id;
    private String name;
    private double monthlyBudget;

    /**
     * Constructor for creating a NEW user (before saving to DB -- id not yet assigned).
     *
     * DAY 2: Now validates inputs upfront.
     * WHY validate in constructor? Because an object should NEVER exist in an invalid state.
     * This is called "maintaining class invariants" -- a core OOP principle.
     */
    public User(String name, double monthlyBudget) {
        setName(name);                 // Reuse setter validation (DRY principle)
        setMonthlyBudget(monthlyBudget);
    }

    /**
     * Constructor for loading an EXISTING user from the database (id is known).
     */
    public User(int id, String name, double monthlyBudget) {
        this.id = id;
        setName(name);
        setMonthlyBudget(monthlyBudget);
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

    /**
     * DAY 2: Added name validation -- name cannot be null or blank.
     * WHY? A user without a name is meaningless. Fail fast instead of
     * discovering the problem later when inserting into the database.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty.");
        }
        this.name = name.trim();  // Trim whitespace for consistency
    }

    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    /**
     * Setter with validation -- budget must be non-negative.
     * WHY validate here? This is the whole point of encapsulation: control HOW data is modified.
     */
    public void setMonthlyBudget(double monthlyBudget) {
        if (monthlyBudget < 0) {
            throw new IllegalArgumentException("Monthly budget cannot be negative.");
        }
        this.monthlyBudget = monthlyBudget;
    }

    // ======================== equals, hashCode, toString ========================

    /**
     * DAY 2: equals() -- Two User objects are "equal" if they have the same id.
     *
     * WHY override equals()?
     * - The default equals() uses '==' (reference equality -- same memory address).
     * - We want LOGICAL equality: two User objects representing the same DB row should be equal.
     * - Collections like ArrayList.contains() and HashMap use equals() for comparison.
     *
     * The standard pattern:
     * 1. Check if same reference (optimization)
     * 2. Check if null or different class
     * 3. Cast and compare the identifying field(s)
     *
     * Viva Tip: "equals() checks logical equality, == checks reference equality.
     *            For objects, always use equals() unless you want to check same memory address."
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;                          // Same reference
        if (obj == null || getClass() != obj.getClass()) return false;  // Null or different type
        User other = (User) obj;                               // Safe cast
        return this.id == other.id;                            // Compare by id
    }

    /**
     * DAY 2: hashCode() -- Must be consistent with equals().
     *
     * CONTRACT: If a.equals(b) is true, then a.hashCode() == b.hashCode() must also be true.
     * WHY? HashMap uses hashCode() to find the bucket, then equals() to find the exact entry.
     * If hashCode() is inconsistent, HashMap will look in the wrong bucket and never find the object.
     *
     * Objects.hash() is a utility method that generates a hash from the given fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * toString() -- called automatically when you print a User object.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monthlyBudget=Rs." + String.format("%.2f", monthlyBudget) +
                '}';
    }
}

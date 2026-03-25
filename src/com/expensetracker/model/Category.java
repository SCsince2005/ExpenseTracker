package com.expensetracker.model;

/**
 * Category enum represents the different types of expenses a student can have.
 *
 * WHY an enum?
 * - Enums restrict values to a fixed set, preventing invalid categories like typos ("Foood").
 * - They are type-safe: the compiler catches errors at compile time, not at runtime.
 * - Each enum constant is a singleton instance — memory efficient.
 * - This is a key OOP concept (type safety + encapsulation of valid values).
 *
 * Viva Tip: "Enums in Java are full classes that can have fields, methods, and constructors,
 *            unlike simple integer constants in C."
 */
public enum Category {
    FOOD("Food & Dining"),
    TRANSPORT("Transport & Travel"),
    ENTERTAINMENT("Entertainment"),
    STUDY("Books & Study Material"),
    RENT("Hostel Rent"),
    OTHER("Miscellaneous");

    // Each enum constant can hold data — this is the display-friendly label
    private final String displayName;

    /**
     * Private constructor — enum constructors are ALWAYS private (even if you omit the keyword).
     * WHY? Because you can't create new enum instances with 'new' — the fixed set is defined above.
     */
    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Override toString so that when you print a Category, it shows the friendly name.
     * e.g., System.out.println(Category.FOOD) → "Food & Dining"
     */
    @Override
    public String toString() {
        return displayName;
    }
}

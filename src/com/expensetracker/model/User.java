package com.expensetracker.model;

import java.util.Objects;

// Maps to the 'users' table in the database
public class User {

    private int id;
    private String name;
    private double monthlyBudget;

    // used when creating a new user (id gets assigned by the DB)
    public User(String name, double monthlyBudget) {
        setName(name);
        setMonthlyBudget(monthlyBudget);
    }

    // used when loading an existing user from DB
    public User(int id, String name, double monthlyBudget) {
        this.id = id;
        setName(name);
        setMonthlyBudget(monthlyBudget);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public double getMonthlyBudget() { return monthlyBudget; }

    public void setMonthlyBudget(double monthlyBudget) {
        if (monthlyBudget < 0) {
            throw new IllegalArgumentException("Monthly budget cannot be negative.");
        }
        this.monthlyBudget = monthlyBudget;
    }

    // equals and hashCode based on id - needed for collections like HashMap/HashSet
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', budget=Rs." + String.format("%.2f", monthlyBudget) + "}";
    }
}

package com.expensetracker.service;

import com.expensetracker.dao.UserDAO;
import com.expensetracker.exception.ExpenseTrackerException;
import com.expensetracker.model.User;

import java.util.List;
import java.util.Optional;

// business logic for users - validates then delegates storage to UserDAO
public class UserService {

    private final UserDAO userDAO;
    private final ExpenseService expenseService;

    public UserService(UserDAO userDAO, ExpenseService expenseService) {
        this.userDAO = userDAO;
        this.expenseService = expenseService;
    }

    public User registerUser(String name, double monthlyBudget) throws ExpenseTrackerException {
        if (name == null || name.trim().isEmpty()) {
            throw new ExpenseTrackerException("User name cannot be null or empty.");
        }
        if (monthlyBudget < 0) {
            throw new ExpenseTrackerException("Monthly budget cannot be negative.");
        }

        User user = new User(name.trim(), monthlyBudget);
        return userDAO.insert(user);
    }

    public Optional<User> getUserById(int id) throws ExpenseTrackerException {
        return userDAO.getById(id);
    }

    public void updateBudget(int userId, double newBudget) throws ExpenseTrackerException {
        if (newBudget < 0) {
            throw new ExpenseTrackerException("Budget cannot be negative.");
        }
        userDAO.updateBudget(userId, newBudget);
    }

    // remaining = budget - total spent
    public double getRemainingBudget(int userId) throws ExpenseTrackerException {
        User user = userDAO.getById(userId)
                .orElseThrow(() -> new ExpenseTrackerException("User with ID " + userId + " not found."));

        double totalSpent = expenseService.getTotalByUser(userId);
        return user.getMonthlyBudget() - totalSpent;
    }

    public List<User> getAllUsers() throws ExpenseTrackerException {
        return userDAO.getAll();
    }
}

package com.expensetracker.service;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.exception.ExpenseTrackerException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// business logic for expenses - validates then delegates storage to ExpenseDAO
public class ExpenseService {

    private final ExpenseDAO expenseDAO;

    public ExpenseService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    public void addExpense(Expense expense) throws ExpenseTrackerException {
        validateExpense(expense);
        expenseDAO.insert(expense);
    }

    private void validateExpense(Expense expense) throws ExpenseTrackerException {
        if (expense == null) {
            throw new ExpenseTrackerException("Expense cannot be null.");
        }
        if (expense.getAmount() <= 0) {
            throw new ExpenseTrackerException("Amount must be greater than zero.");
        }
        if (expense.getCategory() == null) {
            throw new ExpenseTrackerException("Category is required.");
        }
        if (expense.getDate() == null) {
            throw new ExpenseTrackerException("Date is required.");
        }
        if (expense.getUserId() <= 0) {
            throw new ExpenseTrackerException("Must be linked to a valid user.");
        }
    }

    public List<Expense> getExpensesByUser(int userId) throws ExpenseTrackerException {
        return expenseDAO.getByUserId(userId);
    }

    public double getTotalByUser(int userId) throws ExpenseTrackerException {
        return expenseDAO.getTotalByUser(userId);
    }

    public List<Expense> getExpensesByCategory(int userId, Category category) throws ExpenseTrackerException {
        return expenseDAO.getByUserAndCategory(userId, category);
    }

    // builds a category -> total spending map using HashMap
    public Map<Category, Double> getCategoryWiseTotals(int userId) throws ExpenseTrackerException {
        Map<Category, Double> totals = new HashMap<>();

        for (Expense expense : getExpensesByUser(userId)) {
            totals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }
        return totals;
    }
}

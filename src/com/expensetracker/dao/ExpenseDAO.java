package com.expensetracker.dao;

import com.expensetracker.exception.ExpenseTrackerException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// handles all database operations for the expenses table
public class ExpenseDAO {

    public Expense insert(Expense expense) throws ExpenseTrackerException {
        String sql = "INSERT INTO expenses (user_id, category, amount, date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, expense.getUserId());
            pstmt.setString(2, expense.getCategory().name());  // store enum name as string
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getDate().toString());  // LocalDate -> "2026-03-27"
            pstmt.executeUpdate();

            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) {
                expense.setId(keys.getInt(1));
            }
            return expense;

        } catch (SQLException e) {
            throw new ExpenseTrackerException("Failed to insert expense: " + e.getMessage(), e);
        }
    }

    public List<Expense> getByUserId(int userId) throws ExpenseTrackerException {
        String sql = "SELECT id, user_id, category, amount, date FROM expenses WHERE user_id = ?";
        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                expenses.add(mapRow(rs));
            }
            return expenses;

        } catch (SQLException e) {
            throw new ExpenseTrackerException("Failed to fetch expenses: " + e.getMessage(), e);
        }
    }

    public List<Expense> getByUserAndCategory(int userId, Category category) throws ExpenseTrackerException {
        String sql = "SELECT id, user_id, category, amount, date FROM expenses "
                + "WHERE user_id = ? AND category = ?";
        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, category.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                expenses.add(mapRow(rs));
            }
            return expenses;

        } catch (SQLException e) {
            throw new ExpenseTrackerException("Failed to fetch expenses by category: " + e.getMessage(), e);
        }
    }

    // uses SQL SUM() to calculate total on the DB side (more efficient than loading all rows)
    public double getTotalByUser(int userId) throws ExpenseTrackerException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM expenses WHERE user_id = ?";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0.0;

        } catch (SQLException e) {
            throw new ExpenseTrackerException("Failed to get total: " + e.getMessage(), e);
        }
    }

    // converts a ResultSet row into an Expense object
    private Expense mapRow(ResultSet rs) throws SQLException {
        return new Expense(
                rs.getInt("id"),
                rs.getInt("user_id"),
                Category.valueOf(rs.getString("category")),  // string back to enum
                rs.getDouble("amount"),
                LocalDate.parse(rs.getString("date"))        // string back to LocalDate
        );
    }
}

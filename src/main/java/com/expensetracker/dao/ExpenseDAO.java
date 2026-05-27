package com.expensetracker.dao;

import com.expensetracker.models.Expense;
import com.expensetracker.utils.DBConnection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class ExpenseDAO {
	
	//Get all expenses, filtered by category
	public List<Expense> getAllExpenses(String category) throws SQLException{
		List<Expense> expenses = new ArrayList<>();
		String sql;
		
		if (category != null && !category.isEmpty()){
			sql = "SELECT * FROM expenses where category = ? ORDER BY expense_date DESC";
		} else {
			sql = "SELECT * FROM expenses ORDER BY expense_date DESC";
		}
		
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			if (category != null && !category.isEmpty()) {
				stmt.setString(1, category);
			}
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				expenses.add(mapRow(rs));
			}
		}
		return expenses;		
	}

	//Get One expense by id
	public Expense getExpenseById(int Id) throws SQLException{
		String sql = "SELECT * FROM expenses where id = ?";
		
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, Id);
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()){
				return mapRow(rs);
			}	
		}
			return null;		
	}
	
	//CREATE new expense
	public boolean addExpense(Expense expense) throws SQLException{
		String sql = "INSERT INTO expenses (description, amount, category, expense_date) VALUES (?, ?, ?, ?)";
		
		try(Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, expense.getDescription());
			stmt.setBigDecimal(2, expense.getAmount());
			stmt.setString(3, expense.getCategory());
			stmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
			
			return stmt.executeUpdate() > 0;
		}
	}
	
	//UPDATE existing expense
	public boolean updateExpense(Expense expense) throws SQLException{
		String sql = "UPDATE expenses SET description=?, amount=?, category=?, expenseDate=? WHERE id=?";
		
		try(Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, expense.getDescription());
			stmt.setBigDecimal(2, expense.getAmount());
			stmt.setString(3, expense.getCategory());
			stmt.setDate(4, Date.valueOf(expense.getExpenseDate()));
			stmt.setInt(5, expense.getId());
			
			return stmt.executeUpdate() > 0;
		}
	}
	
	//DELETE expense by ID
	public boolean deleteExpense(int id) throws SQLException{
		String sql = "DELETE FROM expenses where id = ?";
		
		try(Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, id);
			
			return stmt.executeUpdate() > 0;
		}
	}
	
	//GET monthly summary
	public List<String[]> getMonthlySummary() throws SQLException{
		String sql = "SELECT category, SUM(amount) as total " +"FROM expenses " +"WHERE MONTH(expense_date) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(expense_date) = YEAR(CURRENT_DATE()) " +
                "GROUP BY category " +
                "ORDER BY total DESC";
		
		List<String[]> summary = new ArrayList<>();
		
		try(Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()){
			
			while (rs.next()){
				summary.add(new String[]{
					rs.getString("category"),
					rs.getBigDecimal("total").toString()
				});
			}
		}
		
		return summary;
	}
	
	//Private helper - converts a ResultSet row into an Expense object
	public Expense mapRow(ResultSet rs) throws SQLException{
		return new Expense(
				rs.getInt("id"),
				rs.getString("description"),
				rs.getBigDecimal("amount"),
				rs.getString("category"),
				rs.getDate("expesne_date").toLocalDate()
				);
	}
}

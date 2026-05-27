package com.expensetracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseModel{
	
	private int id;
	private String description;
	private BigDecimal amount;
	private String category;
	private LocalDate expenseDate;
	
	//Constructor for creating a new expense (no id because DB guarantees it)
	public ExpenseModel(String description, BigDecimal amount, String category, LocalDate expenseDate) {
		this.description = description;
		this.amount = amount;
		this.category = category;
		this.expenseDate = expenseDate;
	}
	
	//Constructor for fetching an existing expense
	public ExpenseModel(int id, String description, BigDecimal amount, String category, LocalDate expenseDate) {
		this.id = id;
		this.description = description;
		this.amount = amount;
		this.category = category;
		this.expenseDate = expenseDate;
	}
	
	//getters
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public String getCategory() {
		return category;
	}
	
	public LocalDate getExpenseDate() {
		return expenseDate;
	}
	
	//setters
	public void setId(int id) {
		this.id = id;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public void setExpenseDate(LocalDate expenseDate) {
		this.expenseDate = expenseDate;
	}
}

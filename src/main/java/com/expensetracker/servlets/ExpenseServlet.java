package com.expensetracker.servlets;

import com.expensetracker.dao.ExpenseDAO;
import com.expensetracker.models.Expense;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/api/expenses/*")
public class ExpenseServlet extends HttpServlet {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    // GET - fetch all expenses or one by ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/expenses or GET /api/expenses?category=Food
                String category = request.getParameter("category");
                List<Expense> expenses = expenseDAO.getAllExpenses(category);
                JSONArray array = new JSONArray();
                for (Expense e : expenses) {
                    array.put(expenseToJson(e));
                }
                response.getWriter().write(array.toString());

            } else {
                // GET /api/expenses/3
                int id = Integer.parseInt(pathInfo.substring(1));
                Expense expense = expenseDAO.getExpenseById(id);
                if (expense != null) {
                    response.getWriter().write(expenseToJson(expense).toString());
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Expense not found\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    // POST - create new expense
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JSONObject json = readRequestBody(request);
            Expense expense = new Expense(
                json.getString("description"),
                new BigDecimal(json.getString("amount")),
                json.getString("category"),
                LocalDate.parse(json.getString("expense_date"))
            );

            boolean created = expenseDAO.addExpense(expense);
            if (created) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\":\"Expense created successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Failed to create expense\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    // DELETE - delete expense by ID
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID required for delete\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean deleted = expenseDAO.deleteExpense(id);
            if (deleted) {
                response.getWriter().write("{\"message\":\"Expense deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Expense not found\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    // Helper - read JSON from request body
    private JSONObject readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return new JSONObject(sb.toString());
    }

    // Helper - convert Expense object to JSONObject
    private JSONObject expenseToJson(Expense expense) {
        JSONObject json = new JSONObject();
        json.put("id", expense.getId());
        json.put("description", expense.getDescription());
        json.put("amount", expense.getAmount());
        json.put("category", expense.getCategory());
        json.put("expense_date", expense.getExpenseDate().toString());
        return json;
    }
}
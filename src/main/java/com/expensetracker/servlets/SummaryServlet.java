package com.expensetracker.servlets;

import com.expensetracker.dao.ExpenseDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

@WebServlet("/api/summary")
public class SummaryServlet extends HttpServlet {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<String[]> summary = expenseDAO.getMonthlySummary();

            JSONObject json = new JSONObject();
            JSONObject breakdown = new JSONObject();
            double total = 0;

            for (String[] row : summary) {
                double amount = Double.parseDouble(row[1]);
                breakdown.put(row[0], amount);
                total += amount;
            }

            json.put("month", YearMonth.now().toString());
            json.put("total", total);
            json.put("breakdown", breakdown);

            response.getWriter().write(json.toString());

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }
}
const API_BASE = 'api/expenses';

// Load everything on page start
window.onload = function () {
    loadExpenses();
    loadSummary();
    document.getElementById('expense-date').valueAsDate = new Date();
};

// LOAD all expenses (with optional category filter)
function loadExpenses(category = '') {
    const url = category ? `${API_BASE}?category=${category}` : API_BASE;

    fetch(url)
        .then(res => res.json())
        .then(data => renderTable(data))
        .catch(err => console.error('Error loading expenses:', err));
}

// FILTER by category
function filterExpenses() {
    const category = document.getElementById('filter-category').value;
    loadExpenses(category);
}

// RENDER expenses into the table
function renderTable(expenses) {
    const tbody = document.getElementById('expenses-table');

    if (expenses.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:#666">No expenses found</td></tr>';
        return;
    }

    tbody.innerHTML = expenses.map(e => `
        <tr>
            <td>${e.expense_date}</td>
            <td>${e.description}</td>
            <td><span class="category-badge">${e.category}</span></td>
            <td>NPR ${parseFloat(e.amount).toLocaleString()}</td>
            <td><button class="delete-btn" onclick="deleteExpense(${e.id})">Delete</button></td>
        </tr>
    `).join('');
}

// ADD new expense
function addExpense() {
    const description = document.getElementById('description').value.trim();
    const amount = document.getElementById('amount').value.trim();
    const category = document.getElementById('category').value;
    const expenseDate = document.getElementById('expense-date').value;
    const messageEl = document.getElementById('form-message');

    if (!description || !amount || !category || !expenseDate) {
        messageEl.textContent = 'Please fill in all fields.';
        messageEl.className = 'error';
        return;
    }

    fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ description, amount, category, expense_date: expenseDate })
    })
    .then(res => res.json())
    .then(data => {
        messageEl.textContent = data.message || data.error;
        messageEl.className = data.message ? 'success' : 'error';
        if (data.message) {
            clearForm();
            loadExpenses();
            loadSummary();
        }
    })
    .catch(err => console.error('Error adding expense:', err));
}

// DELETE expense
function deleteExpense(id) {
    if (!confirm('Delete this expense?')) return;

    fetch(`${API_BASE}/${id}`, { method: 'DELETE' })
        .then(res => res.json())
        .then(() => {
            loadExpenses();
            loadSummary();
        })
        .catch(err => console.error('Error deleting expense:', err));
}

// LOAD monthly summary
function loadSummary() {
    fetch('api/summary')
        .then(res => res.json())
        .then(data => {
            document.getElementById('total-amount').textContent =
                'NPR ' + parseFloat(data.total).toLocaleString();

            const breakdownEl = document.getElementById('breakdown');
            breakdownEl.innerHTML = '';

            if (data.breakdown) {
                Object.entries(data.breakdown).forEach(([category, amount]) => {
                    const div = document.createElement('div');
                    div.className = 'breakdown-item';
                    div.innerHTML = `${category}: <span>NPR ${parseFloat(amount).toLocaleString()}</span>`;
                    breakdownEl.appendChild(div);
                });
            }
        })
        .catch(err => console.error('Error loading summary:', err));
}

// CLEAR form after successful add
function clearForm() {
    document.getElementById('description').value = '';
    document.getElementById('amount').value = '';
    document.getElementById('category').value = '';
    document.getElementById('expense-date').valueAsDate = new Date();
}
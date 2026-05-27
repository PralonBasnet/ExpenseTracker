CREATE DATABASE expense_tracker;
USE expense_tracker;

CREATE TABLE expenses(
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    expense_date DATE NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO expenses (description, amount, category, expense_date) VALUES
('Lunch at Thamel', 450.00, 'Food', '2026-05-20'),
('Bus fare', 25.00, 'Transport', '2026-05-21'),
('Electricity bill', 1200.00, 'Utilities', '2026-05-22'),
('Movie ticket', 500.00, 'Entertainment', '2026-05-23'),
('Paracetamol', 150.00, 'Health', '2026-05-24'),
('Dinner with friends', 800.00, 'Food', '2026-05-25'),
('Taxi', 350.00, 'Transport', '2026-05-26');
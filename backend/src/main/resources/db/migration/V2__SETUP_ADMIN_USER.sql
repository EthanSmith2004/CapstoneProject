-- Users
INSERT INTO "user" (id, first_name, last_name, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at, updated_at) VALUES
(1, 'Admin', 'User', 'admin@demo.com', '{bcrypt}$2a$12$GGsMs5XbpO7ijjiZfki2nOmWfcMLfvMzZQa.UkL2OAO8BzOL1bmL.', true, true, true, true, NOW(), NOW()); -- password: admin123

-- Roles
INSERT INTO user_roles (user_id, roles) VALUES
(1, 'ADMIN'),
(1, 'USER_ADMIN'),
(1, 'FINANCIAL_ADMIN'),
(1, 'MENU_ADMIN'),
(1, 'ORDER_ADMIN'),
(1, 'REPORTING_ADMIN'),
(1, 'FEEDBACK_ADMIN'),
(1, 'AUDIT_ADMIN');

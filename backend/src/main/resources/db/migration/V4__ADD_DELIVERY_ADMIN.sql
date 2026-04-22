-- Add delivery admin user for testing the barcode scanning system
-- Password: delivery123 (BCrypt hashed)

INSERT INTO "user" (id, first_name, last_name, email, password, account_non_expired, account_non_locked, credentials_non_expired, enabled, created_at, updated_at)
VALUES (
    nextval('user_seq'),
    'Delivery',
    'Driver',
    'delivery@demo.com',
    '{bcrypt}$2a$12$c8j9fMeJZ7z6N4NknmT5i.ImMGItFj9/fuXdEG6LoKHG/PFON6.O.',
    true,
    true,
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- Add DELIVERY_ADMIN role to the delivery user
INSERT INTO user_roles (user_id, roles)
SELECT id, 'DELIVERY_ADMIN'
FROM "user"
WHERE email = 'delivery@demo.com'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, roles)
SELECT id, 'ADMIN'
FROM "user"
WHERE email = 'delivery@demo.com'
ON CONFLICT DO NOTHING;

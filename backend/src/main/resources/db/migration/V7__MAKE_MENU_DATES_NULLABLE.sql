-- Make menu item date fields nullable to support draft items
ALTER TABLE menu_item ALTER COLUMN release_date DROP NOT NULL;
ALTER TABLE menu_item ALTER COLUMN order_by DROP NOT NULL;

-- Add date_time_delivered column to order_item table
ALTER TABLE order_item ADD COLUMN date_time_delivered TIMESTAMP WITHOUT TIME ZONE;

-- Add index for better query performance on delivered items
CREATE INDEX idx_order_item_date_time_delivered ON order_item(date_time_delivered);

-- Add comment to document the column
COMMENT ON COLUMN order_item.date_time_delivered IS 'Timestamp when the item was marked as delivered by delivery admin';

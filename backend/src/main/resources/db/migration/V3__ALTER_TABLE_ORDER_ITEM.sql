ALTER TABLE order_item
    ADD refund_transaction_id BIGINT,
    ADD status VARCHAR(255) DEFAULT 'DELIVERED' NOT NULL;

ALTER TABLE order_item
    ADD CONSTRAINT FK_ORDER_ITEM_ON_REFUND_TRANSACTION FOREIGN KEY (refund_transaction_id) REFERENCES transaction (id);
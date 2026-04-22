ALTER TABLE order_item_feedback
    ADD category VARCHAR(255);

ALTER TABLE order_item_feedback
    ADD sentiment BIGINT;
CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_ticket_category_id ON order_items (ticket_category_id);
CREATE INDEX idx_tickets_order_item_id ON tickets (order_item_id);
CREATE INDEX idx_ticket_categories_event_id ON ticket_categories (event_id);
CREATE UNIQUE INDEX idx_tickets_code ON tickets (ticket_code);

DROP INDEX IF EXISTS idx_events_start_datetime;
DROP INDEX IF EXISTS idx_order_items_order_id;
DROP INDEX IF EXISTS idx_tickets_order_item_id;

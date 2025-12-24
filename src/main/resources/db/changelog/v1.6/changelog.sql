DROP INDEX IF EXISTS idx_orders_user_id;
DROP INDEX IF EXISTS idx_orders_status;
DROP INDEX IF EXISTS idx_order_items_order_id;
DROP INDEX IF EXISTS idx_order_items_ticket_category_id;
DROP INDEX IF EXISTS idx_tickets_order_item_id;
DROP INDEX IF EXISTS idx_ticket_categories_event_id;
DROP INDEX IF EXISTS idx_tickets_code;

CREATE INDEX IF NOT EXISTS idx_events_start_datetime ON events (start_datetime DESC);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items (order_id);
CREATE INDEX IF NOT EXISTS idx_tickets_order_item_id ON tickets (order_item_id);
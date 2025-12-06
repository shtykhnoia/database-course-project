CREATE TABLE orders
(
    id           BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(100) UNIQUE NOT NULL,
    user_id      BIGINT              NOT NULL REFERENCES users (id),
    status       VARCHAR(20) DEFAULT 'pending',
    total_amount DECIMAL(10, 2)      NOT NULL,
    created_at   TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    CHECK (status IN ('pending', 'confirmed', 'cancelled', 'expired')),
    CHECK (total_amount >= 0)
);

CREATE TABLE order_items
(
    id                 BIGSERIAL PRIMARY KEY,
    order_id           BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    ticket_category_id BIGINT         NOT NULL REFERENCES ticket_categories (id),
    quantity           INT            NOT NULL,
    unit_price         DECIMAL(10, 2) NOT NULL,
    CHECK (quantity > 0),
    CHECK (unit_price >= 0)
);

CREATE TABLE payments
(
    id                  BIGSERIAL PRIMARY KEY,
    order_id            BIGINT         NOT NULL REFERENCES orders (id),
    external_payment_id VARCHAR(255),
    amount              DECIMAL(10, 2) NOT NULL,
    status              VARCHAR(20) DEFAULT 'pending',
    paid_at             TIMESTAMP,
    CHECK (status IN ('pending', 'succeeded', 'failed')),
    CHECK (amount >= 0)
);

CREATE TABLE tickets
(
    id             BIGSERIAL PRIMARY KEY,
    ticket_code    VARCHAR(100) UNIQUE NOT NULL,
    order_item_id  BIGINT              NOT NULL REFERENCES order_items (id),
    attendee_name  VARCHAR(200),
    attendee_email VARCHAR(255),
    status         VARCHAR(20) DEFAULT 'active',
    CHECK (status IN ('active', 'checked_in', 'cancelled'))
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_ticket_category_id ON order_items (ticket_category_id);
CREATE INDEX idx_tickets_order_item_id ON tickets (order_item_id);
CREATE UNIQUE INDEX idx_tickets_code ON tickets (ticket_code);

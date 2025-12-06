CREATE TABLE ticket_categories
(
    id                 BIGSERIAL PRIMARY KEY,
    event_id           BIGINT         NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    name               VARCHAR(100)   NOT NULL,
    description        TEXT,
    price              DECIMAL(10, 2) NOT NULL,
    quantity_available INT            NOT NULL,
    sale_start_date    TIMESTAMP,
    sale_end_date      TIMESTAMP,
    CHECK (quantity_available >= 0),
    CHECK (price >= 0)
);

CREATE INDEX idx_ticket_categories_event_id ON ticket_categories (event_id);

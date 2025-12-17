CREATE TABLE venues
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    address  VARCHAR(500),
    capacity INT,
    CHECK (capacity > 0)
);

ALTER TABLE events
    ADD COLUMN venue_id BIGINT REFERENCES venues (id);

ALTER TABLE events
    ADD COLUMN end_datetime TIMESTAMP;

ALTER TABLE events
    DROP CONSTRAINT IF EXISTS events_event_status_check;

ALTER TABLE events
    ADD CONSTRAINT events_event_status_check
        CHECK (event_status IN ('draft', 'published', 'cancelled', 'completed'));

CREATE TABLE promo_codes
(
    id             BIGSERIAL PRIMARY KEY,
    code           VARCHAR(50) UNIQUE NOT NULL,
    event_id       BIGINT REFERENCES events (id),
    discount_type  VARCHAR(10)        NOT NULL,
    discount_value DECIMAL(10, 2)     NOT NULL,
    max_uses       INT,
    used_count     INT DEFAULT 0      NOT NULL,
    valid_from     TIMESTAMP,
    valid_until    TIMESTAMP,
    CHECK (discount_type IN ('percent', 'fixed')),
    CHECK (
        (discount_type = 'percent' AND discount_value BETWEEN 0 AND 100) OR
        (discount_type = 'fixed' AND discount_value >= 0)
        ),
    CHECK (used_count >= 0)
);

ALTER TABLE order_items
    ADD COLUMN IF NOT EXISTS promo_code_id BIGINT REFERENCES promo_codes (id);

CREATE TABLE event_tags
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE event_tag_assignments
(
    id       BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    tag_id   BIGINT NOT NULL REFERENCES event_tags (id) ON DELETE CASCADE,
    UNIQUE (event_id, tag_id)
);

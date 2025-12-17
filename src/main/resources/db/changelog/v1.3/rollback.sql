DROP TABLE IF EXISTS event_tag_assignments;
DROP TABLE IF EXISTS event_tags;
DROP TABLE IF EXISTS promo_codes;

ALTER TABLE order_items
    DROP COLUMN IF EXISTS promo_code_id;

ALTER TABLE events
    DROP CONSTRAINT IF EXISTS events_event_status_check;

ALTER TABLE events
    ADD CONSTRAINT events_event_status_check
        CHECK (event_status IN ('draft', 'published', 'cancelled'));

ALTER TABLE events
    DROP COLUMN IF EXISTS end_datetime;

ALTER TABLE events
    DROP COLUMN IF EXISTS venue_id;

DROP TABLE IF EXISTS venues;

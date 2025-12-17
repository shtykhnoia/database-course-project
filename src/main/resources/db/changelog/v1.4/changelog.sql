CREATE OR REPLACE VIEW event_sales_view AS
SELECT e.id AS                         event_id,
       e.title                         event_title,
       tc.id                           category_id,
       tc.name                         category_name,
       COALESCE(SUM(oi.quantity), 0)   tickets_sold,
       tc.quantity_available           tickets_available,
       COALESCE(AVG(oi.unit_price), 0) avg_ticket_price,
       COALESCE(SUM(oi.quantity) FILTER (WHERE o.created_at >= now() - INTERVAL '7 days'),
                0)                     tickets_sold_last_7_days,
       CASE
           WHEN (tc.quantity_available + COALESCE(SUM(oi.quantity), 0)) > 0
               THEN (COALESCE(SUM(oi.quantity), 0) * 100 / (tc.quantity_available + COALESCE(SUM(oi.quantity), 0)))::INTEGER
           ELSE 0
           END                         sold_percentage
FROM events e
         JOIN ticket_categories tc ON tc.event_id = e.id
         LEFT JOIN order_items oi ON oi.ticket_category_id = tc.id
         LEFT JOIN orders o ON o.id = oi.order_id AND o.status = 'confirmed'
GROUP BY e.id, e.title, tc.id, tc.name, tc.quantity_available;

CREATE OR REPLACE VIEW promo_code_effectiveness_view AS
SELECT pc.id                                         AS promo_code_id,
       pc.code,
       pc.discount_type,
       pc.discount_value,
       pc.max_uses,
       pc.used_count,
       pc.event_id,
       MAX(e.title)                                  event_title,
       COUNT(DISTINCT oi.order_id)                   orders_with_promo,
       COALESCE(SUM(oi.unit_price * oi.quantity), 0) total_sales_with_promo,
       CASE
           WHEN pc.discount_type = 'percent' THEN
               COALESCE(SUM(oi.unit_price * oi.quantity * pc.discount_value / 100), 0)
           WHEN pc.discount_type = 'fixed' THEN
               pc.discount_value * COUNT(DISTINCT oi.order_id)
           ELSE 0
           END                                       total_discount_given,
       CASE
           WHEN pc.discount_type = 'percent' THEN
               COALESCE(AVG(oi.unit_price * oi.quantity * pc.discount_value / 100), 0)
           WHEN pc.discount_type = 'fixed' THEN
               pc.discount_value
           ELSE 0
           END                                       avg_discount_per_order,
       CASE
           WHEN pc.max_uses > 0 THEN (pc.used_count * 100 / pc.max_uses)::INTEGER
           END                                       usage_percentage,
       CASE
           WHEN pc.valid_until < NOW() THEN 'expired'
           WHEN pc.valid_from > NOW() THEN 'not_active_yet'
           WHEN pc.used_count >= pc.max_uses THEN 'limit_reached'
           ELSE 'active'
           END                                       promo_status
FROM promo_codes pc
         LEFT JOIN events e ON e.id = pc.event_id
         LEFT JOIN order_items oi ON oi.promo_code_id = pc.id
         LEFT JOIN orders o ON o.id = oi.order_id AND o.status = 'confirmed'
GROUP BY pc.id
ORDER BY total_discount_given DESC;

CREATE OR REPLACE VIEW venue_utilization_view AS
SELECT
    v.id AS venue_id,
    v.name AS venue_name,
    v.address,
    v.capacity,
    COUNT(DISTINCT e.id) AS total_events,
    COUNT(DISTINCT CASE WHEN e.event_status = 'published' THEN e.id END) AS active_events,
    COALESCE(SUM(CASE WHEN o.status = 'confirmed' THEN oi.quantity ELSE 0 END), 0) AS tickets_sold,
    COALESCE(SUM(CASE WHEN o.status = 'confirmed' THEN o.total_amount ELSE 0 END), 0) AS total_revenue,
    CASE
        WHEN v.capacity > 0 AND COUNT(DISTINCT e.id) > 0 THEN
            (COALESCE(SUM(CASE WHEN o.status = 'confirmed' THEN oi.quantity ELSE 0 END), 0) * 100 /
             (v.capacity * COUNT(DISTINCT e.id)))::INTEGER
        END AS avg_occupancy_percentage
FROM venues v
         LEFT JOIN events e ON e.venue_id = v.id
         LEFT JOIN ticket_categories tc ON tc.event_id = e.id
         LEFT JOIN order_items oi ON oi.ticket_category_id = tc.id
         LEFT JOIN orders o ON o.id = oi.order_id
GROUP BY v.id
ORDER BY total_revenue DESC;

package com.example.ticketingsystem.config;

import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class TestDataGenerator implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TestDataGenerator.class);
    private final JdbcTemplate jdbcTemplate;
    private final Faker faker = new Faker(new Locale("ru"));

    @Value("${app.generate-test-data:false}")
    private boolean generateTestData;

    public TestDataGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (!generateTestData) {
            log.info("Test data generation is disabled");
            return;
        }

        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        if (userCount != null && userCount > 0) {
            log.info("Database already contains data. Skipping test data generation.");
            return;
        }

        log.info("Starting test data generation...");
        long startTime = System.currentTimeMillis();

        generateAllData();

        long endTime = System.currentTimeMillis();
        log.info("Test data generation completed in {} seconds", (endTime - startTime) / 1000);
    }

    @Transactional
    public void generateAllData() {
        List<Long> userIds = generateUsers(5000);
        generateUserRoles(userIds);
        List<Long> organizerIds = generateOrganizers(100, userIds);
        List<Long> venueIds = generateVenues(150);
        List<Long> eventIds = generateEvents(1000, organizerIds, venueIds);
        List<Long> eventTagIds = generateEventTags(30);
        generateEventTagAssignments(eventIds, eventTagIds);
        Map<Long, List<Long>> eventTicketCategories = generateTicketCategories(eventIds);
        List<Long> promoCodeIds = generatePromoCodes(300, eventIds);
        List<Long> orderIds = generateOrders(50000, userIds);
        generateOrderItems(orderIds, eventTicketCategories, promoCodeIds);
        generatePayments(orderIds);
        generateTickets();
    }

    private List<Long> generateUsers(int count) {
        log.info("Generating {} users...", count);
        List<Long> userIds = new ArrayList<>();

        String sql = """
            INSERT INTO users (username, email, password_hash, first_name, last_name)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (int i = 0; i < count; i++) {
            String username = faker.internet().username() + "_" + i;
            String email = "user" + i + "_" + faker.internet().emailAddress();
            String passwordHash = "$2a$10$" + faker.internet().password(40, 60, true, true);
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();

            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    username, email, passwordHash, firstName, lastName);
            userIds.add(id);

            if ((i + 1) % 1000 == 0) {
                log.info("Generated {} users", i + 1);
            }
        }

        return userIds;
    }

    private void generateUserRoles(List<Long> userIds) {
        log.info("Generating user roles...");

        Long roleUserId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'user'", Long.class);
        Long roleOrganizerId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'organizer'", Long.class);
        Long roleAdminId = jdbcTemplate.queryForObject("SELECT id FROM roles WHERE name = 'admin'", Long.class);

        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i++) {
            Long userId = userIds.get(i);

            if (i < 5) {
                batchArgs.add(new Object[]{userId, roleAdminId});
            } else if (i < 105) {
                batchArgs.add(new Object[]{userId, roleOrganizerId});
            }

            batchArgs.add(new Object[]{userId, roleUserId});
        }

        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?) ON CONFLICT DO NOTHING", batchArgs);
    }

    private List<Long> generateOrganizers(int count, List<Long> userIds) {
        log.info("Generating {} organizers...", count);
        List<Long> organizerIds = new ArrayList<>();

        String sql = """
            INSERT INTO organizers (name, description, contact_email, contact_phone, user_id)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (int i = 0; i < count; i++) {
            String name = faker.company().name();
            String description = faker.lorem().paragraph(3);
            String email = "organizer" + i + "_" + faker.internet().emailAddress();
            String phone = faker.phoneNumber().cellPhone();
            Long userId = i < userIds.size() ? userIds.get(i + 5) : null;

            Long id = jdbcTemplate.queryForObject(sql, Long.class, name, description, email, phone, userId);
            organizerIds.add(id);
        }

        return organizerIds;
    }

    private List<Long> generateVenues(int count) {
        log.info("Generating {} venues...", count);
        List<Long> venueIds = new ArrayList<>();

        String sql = """
            INSERT INTO venues (name, address, capacity)
            VALUES (?, ?, ?)
            RETURNING id
            """;

        for (int i = 0; i < count; i++) {
            String name = faker.company().name() + " " + faker.options().option("Арена", "Холл", "Зал", "Театр", "Стадион", "Клуб");
            String address = faker.address().fullAddress();
            int capacity = faker.number().numberBetween(100, 50000);

            Long id = jdbcTemplate.queryForObject(sql, Long.class, name, address, capacity);
            venueIds.add(id);
        }

        return venueIds;
    }

    private List<Long> generateEvents(int count, List<Long> organizerIds, List<Long> venueIds) {
        log.info("Generating {} events...", count);
        List<Long> eventIds = new ArrayList<>();

        String sql = """
            INSERT INTO events (title, description, start_datetime, end_datetime, venue_id, organizer_id, event_status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (int i = 0; i < count; i++) {
            String title = faker.music().genre() + " " + faker.options().option("Концерт", "Фестиваль", "Шоу", "Вечер", "Ночь");
            String description = faker.lorem().paragraph(5);

            LocalDateTime startDatetime = LocalDateTime.ofInstant(
                    faker.date().future(365, TimeUnit.DAYS).toInstant(),
                    ZoneId.systemDefault()
            );
            LocalDateTime endDatetime = startDatetime.plusHours(faker.number().numberBetween(2, 8));

            Long venueId = venueIds.get(faker.random().nextInt(venueIds.size()));
            Long organizerId = organizerIds.get(faker.random().nextInt(organizerIds.size()));
            String status = faker.options().option("draft", "published", "cancelled", "completed");

            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    title, description, startDatetime, endDatetime, venueId, organizerId, status);
            eventIds.add(id);

            if ((i + 1) % 200 == 0) {
                log.info("Generated {} events", i + 1);
            }
        }

        return eventIds;
    }

    private List<Long> generateEventTags(int count) {
        log.info("Generating {} event tags...", count);
        List<String> tags = Arrays.asList(
                "Концерт", "Театр", "Выставка", "Спорт", "Кино", "Стендап",
                "Фестиваль", "Конференция", "Мастер-класс", "Детям", "Семейное",
                "На открытом воздухе", "Музыка", "Искусство", "Образование",
                "Технологии", "Бизнес", "Развлечения", "Культура", "Наука",
                "Рок", "Поп", "Джаз", "Классика", "Электроника", "Хип-хоп",
                "Танцы", "Опера", "Балет", "Драма"
        );

        List<Long> tagIds = new ArrayList<>();
        String sql = "INSERT INTO event_tags (name) VALUES (?) ON CONFLICT (name) DO UPDATE SET name = EXCLUDED.name RETURNING id";

        for (int i = 0; i < Math.min(count, tags.size()); i++) {
            String tagName = tags.get(i);
            Long id = jdbcTemplate.queryForObject(sql, Long.class, tagName);
            tagIds.add(id);
        }

        return tagIds;
    }

    private void generateEventTagAssignments(List<Long> eventIds, List<Long> eventTagIds) {
        log.info("Generating event-tag relationships...");
        List<Object[]> batchArgs = new ArrayList<>();

        for (Long eventId : eventIds) {
            int tagsCount = faker.number().numberBetween(1, 4);
            Set<Long> selectedTags = new HashSet<>();

            for (int i = 0; i < tagsCount; i++) {
                Long tagId = eventTagIds.get(faker.random().nextInt(eventTagIds.size()));
                if (selectedTags.add(tagId)) {
                    batchArgs.add(new Object[]{eventId, tagId});
                }
            }
        }

        jdbcTemplate.batchUpdate(
                "INSERT INTO event_tag_assignments (event_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                batchArgs
        );
    }

    private Map<Long, List<Long>> generateTicketCategories(List<Long> eventIds) {
        log.info("Generating ticket categories...");
        Map<Long, List<Long>> eventTicketCategories = new HashMap<>();

        String sql = """
            INSERT INTO ticket_categories (event_id, name, description, price, quantity_available, sale_start_date, sale_end_date)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (Long eventId : eventIds) {
            List<Long> categoryIds = new ArrayList<>();
            int categoriesCount = faker.number().numberBetween(2, 5);

            String[] categoryNames = {"VIP", "Фан-зона", "Партер", "Балкон", "Стандарт"};

            for (int i = 0; i < categoriesCount; i++) {
                String name = categoryNames[i % categoryNames.length];
                String description = faker.lorem().sentence();
                BigDecimal price = BigDecimal.valueOf(faker.number().numberBetween(500, 50000));
                int quantity = faker.number().numberBetween(50, 5000);

                LocalDateTime saleStart = LocalDateTime.now().minusDays(faker.number().numberBetween(1, 60));
                LocalDateTime saleEnd = saleStart.plusDays(faker.number().numberBetween(30, 180));

                Long id = jdbcTemplate.queryForObject(sql, Long.class,
                        eventId, name, description, price, quantity, saleStart, saleEnd);
                categoryIds.add(id);
            }

            eventTicketCategories.put(eventId, categoryIds);
        }

        return eventTicketCategories;
    }

    private List<Long> generatePromoCodes(int count, List<Long> eventIds) {
        log.info("Generating {} promo codes...", count);
        List<Long> promoCodeIds = new ArrayList<>();

        String sql = """
            INSERT INTO promo_codes (code, discount_type, discount_value, event_id, valid_from, valid_until, max_uses, used_count)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (int i = 0; i < count; i++) {
            String code = "PROMO" + String.format("%05d", i);
            String discountType = faker.options().option("percent", "fixed");
            BigDecimal discountValue = discountType.equals("percent")
                    ? BigDecimal.valueOf(faker.number().numberBetween(5, 50))
                    : BigDecimal.valueOf(faker.number().numberBetween(100, 5000));

            Long eventId = faker.bool().bool() ? eventIds.get(faker.random().nextInt(eventIds.size())) : null;

            LocalDateTime validFrom = LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30));
            LocalDateTime validUntil = validFrom.plusDays(faker.number().numberBetween(30, 180));
            int maxUses = faker.number().numberBetween(10, 1000);
            int usedCount = faker.number().numberBetween(0, maxUses / 2);

            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    code, discountType, discountValue, eventId, validFrom, validUntil, maxUses, usedCount);
            promoCodeIds.add(id);
        }

        return promoCodeIds;
    }

    private List<Long> generateOrders(int count, List<Long> userIds) {
        log.info("Generating {} orders...", count);
        List<Long> orderIds = new ArrayList<>();

        String sql = """
            INSERT INTO orders (order_number, user_id, total_amount, status, created_at)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;

        for (int i = 0; i < count; i++) {
            String orderNumber = "ORD-" + String.format("%010d", i);
            Long userId = userIds.get(faker.random().nextInt(userIds.size()));
            BigDecimal totalAmount = BigDecimal.valueOf(faker.number().numberBetween(500, 100000));
            String status = faker.options().option("pending", "confirmed", "cancelled", "expired");
            LocalDateTime createdAt = LocalDateTime.ofInstant(
                    faker.date().past(365, TimeUnit.DAYS).toInstant(),
                    ZoneId.systemDefault()
            );

            Long id = jdbcTemplate.queryForObject(sql, Long.class,
                    orderNumber, userId, totalAmount, status, createdAt);
            orderIds.add(id);

            if ((i + 1) % 5000 == 0) {
                log.info("Generated {} orders", i + 1);
            }
        }

        return orderIds;
    }

    private void generateOrderItems(List<Long> orderIds, Map<Long, List<Long>> eventTicketCategories, List<Long> promoCodeIds) {
        log.info("Generating order items...");

        String sql = """
            INSERT INTO order_items (order_id, ticket_category_id, quantity, unit_price, promo_code_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        List<Object[]> batchArgs = new ArrayList<>();

        for (Long orderId : orderIds) {
            int itemsCount = faker.number().numberBetween(1, 3);

            List<Long> eventIds = new ArrayList<>(eventTicketCategories.keySet());
            Long eventId = eventIds.get(faker.random().nextInt(eventIds.size()));
            List<Long> categoryIds = eventTicketCategories.get(eventId);

            for (int i = 0; i < itemsCount && i < categoryIds.size(); i++) {
                Long categoryId = categoryIds.get(i);
                int quantity = faker.number().numberBetween(1, 5);
                BigDecimal unitPrice = BigDecimal.valueOf(faker.number().numberBetween(500, 50000));
                Long promoCodeId = faker.bool().bool() && !promoCodeIds.isEmpty()
                        ? promoCodeIds.get(faker.random().nextInt(promoCodeIds.size()))
                        : null;

                batchArgs.add(new Object[]{orderId, categoryId, quantity, unitPrice, promoCodeId});
            }

            if (batchArgs.size() >= 5000) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
                batchArgs.clear();
                log.info("Generated order items batch");
            }
        }

        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    private void generatePayments(List<Long> orderIds) {
        log.info("Generating payments...");

        String sql = """
            INSERT INTO payments (order_id, amount, status, external_payment_id, paid_at)
            VALUES (?, ?, ?, ?, ?)
            """;

        List<Object[]> batchArgs = new ArrayList<>();

        for (Long orderId : orderIds) {
            BigDecimal amount = BigDecimal.valueOf(faker.number().numberBetween(500, 100000));
            String status = faker.options().option("pending", "succeeded", "failed");
            String externalPaymentId = status.equals("succeeded") ? "PAY-" + faker.number().digits(15) : null;
            LocalDateTime paidAt = status.equals("succeeded")
                    ? LocalDateTime.ofInstant(faker.date().past(365, TimeUnit.DAYS).toInstant(), ZoneId.systemDefault())
                    : null;

            batchArgs.add(new Object[]{orderId, amount, status, externalPaymentId, paidAt});

            if (batchArgs.size() >= 5000) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
                batchArgs.clear();
                log.info("Generated payments batch");
            }
        }

        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    private void generateTickets() {
        log.info("Generating tickets...");

        String selectSql = """
            SELECT oi.id, oi.quantity
            FROM order_items oi
            JOIN orders o ON o.id = oi.order_id
            WHERE o.status = 'confirmed'
            """;

        String insertSql = """
            INSERT INTO tickets (ticket_code, order_item_id, attendee_name, attendee_email, status)
            VALUES (?, ?, ?, ?, ?)
            """;

        List<Map<String, Object>> orderItems = jdbcTemplate.queryForList(selectSql);
        List<Object[]> batchArgs = new ArrayList<>();

        int ticketCounter = 0;
        for (Map<String, Object> item : orderItems) {
            Long orderItemId = ((Number) item.get("id")).longValue();
            int quantity = ((Number) item.get("quantity")).intValue();

            for (int i = 0; i < quantity; i++) {
                String ticketCode = "TKT-" + String.format("%012d", ticketCounter++);
                String attendeeName = faker.name().fullName();
                String attendeeEmail = "ticket" + ticketCounter + "_" + faker.internet().emailAddress();
                String status = faker.options().option("active", "checked_in", "cancelled");

                batchArgs.add(new Object[]{ticketCode, orderItemId, attendeeName, attendeeEmail, status});

                if (batchArgs.size() >= 5000) {
                    jdbcTemplate.batchUpdate(insertSql, batchArgs);
                    batchArgs.clear();
                    log.info("Generated {} tickets", ticketCounter);
                }
            }
        }

        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, batchArgs);
        }

        Integer ticketCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tickets", Integer.class);
        log.info("Total tickets generated: {}", ticketCount);
    }
}

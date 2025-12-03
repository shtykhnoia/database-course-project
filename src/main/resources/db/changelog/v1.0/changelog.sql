CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name varchar(50) UNIQUE NOT NULL
);

INSERT INTO roles (name)
VALUES ('admin'),
       ('user'),
       ('organizer');

CREATE TABLE IF NOT EXISTS users
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(255) UNIQUE NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE user_roles
(
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    role_id INTEGER REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE organizers
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    user_id       BIGINT REFERENCES users (id)
);

CREATE TABLE events
(
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    organizer_id   BIGINT       NOT NULL REFERENCES organizers (id),
    start_datetime TIMESTAMP    NOT NULL,
    event_status   VARCHAR(20) DEFAULT 'draft' CHECK (event_status IN ('draft', 'published', 'cancelled'))
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT    AUTO_INCREMENT    PRIMARY    KEY,
    name    VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS categories
(
    id    INTEGER    AUTO_INCREMENT    PRIMARY    KEY,
    name    VARCHAR(
    50
) NOT NULL UNIQUE,
    is_system BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS events
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    created_on
    TIMESTAMP,

    initiator_id
    BIGINT
    NOT
    NULL,
    category_id
    INTEGER
    NOT
    NULL
    DEFAULT
    1,

    title
    VARCHAR
(
    120
) NOT NULL,
    annotation VARCHAR
(
    2000
) NOT NULL,
    description VARCHAR
(
    7000
) NOT NULL,

    event_date TIMESTAMP NOT NULL,

    paid BOOLEAN NOT NULL,
    participant_limit INTEGER DEFAULT 0,

    published_on TIMESTAMP,

    request_moderation BOOLEAN DEFAULT FALSE,
    confirmed_requests INTEGER DEFAULT 0,
    state VARCHAR
(
    50
) NOT NULL,
    views INTEGER NOT NULL,

    location_lat DOUBLE NOT NULL,
    location_lon DOUBLE NOT NULL,
    CONSTRAINT fk_event_initiator
    FOREIGN KEY
(
    initiator_id
)
    REFERENCES users
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_event_category
    FOREIGN KEY
(
    category_id
)
    REFERENCES categories
(
    id
)
    ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS requests
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL,
    event_id
    BIGINT
    NOT
    NULL,
    status
    VARCHAR
(
    50
) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id
    FOREIGN KEY
(
    user_id
)
    REFERENCES users
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_event_id
    FOREIGN KEY
(
    event_id
)
    REFERENCES events
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT unique_user_event UNIQUE
(
    user_id,
    event_id
)
    );

CREATE TABLE IF NOT EXISTS compilations
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    title
    VARCHAR
(
    50
) NOT NULL,
    pinned BOOLEAN NOT NULL
    );

CREATE TABLE IF NOT EXISTS compilations_events
(
    compilation_id
    BIGINT
    NOT
    NULL,
    event_id
    BIGINT
    NOT
    NULL,

    CONSTRAINT
    pk_compilations_events
    PRIMARY
    KEY
(
    compilation_id,
    event_id
),
    CONSTRAINT fk_compilation
    FOREIGN KEY
(
    compilation_id
)
    REFERENCES compilations
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_event
    FOREIGN KEY
(
    event_id
)
    REFERENCES events
(
    id
)
    ON DELETE CASCADE
    );

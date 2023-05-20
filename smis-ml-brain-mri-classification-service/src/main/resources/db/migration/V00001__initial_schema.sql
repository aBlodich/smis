CREATE SEQUENCE IF NOT EXISTS ml_models_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE ml_models
(
    id      INTEGER         NOT NULL PRIMARY KEY DEFAULT nextval('ml_models_id_seq'),
    name    VARCHAR,
    file_id VARCHAR(255),
    options jsonb,
    active  BOOLEAN
);

CREATE UNIQUE INDEX uq_ml_models_active ON ml_models (active) where active = true;

create table outbox
(
    id                  uuid        not null primary key,
    message_key         text        null,
    topic               text        not null,
    payload             text        not null,
    status              text        not null,
    created_at          timestamp   not null,
    try_count           int         not null,
    last_try_at         timestamp   null,
    error_description   text        null
);

CREATE TABLE shedlock
(
    name       varchar(100)     NOT NULL    PRIMARY KEY,
    lock_until timestamp        NULL,
    locked_at  timestamp        NULL,
    locked_by  varchar(100)     NULL
);
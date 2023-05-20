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
    name       varchar(100)     NOT NULL,
    lock_until timestamp        NULL,
    locked_at  timestamp        NULL,
    locked_by  varchar(100)     NULL,
    CONSTRAINT shedlock_pkey    PRIMARY KEY (name)
);
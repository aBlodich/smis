CREATE SEQUENCE IF NOT EXISTS checking_service_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS diagnosis_types_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE diagnosis_types
(
    id   INTEGER        NOT NULL PRIMARY KEY DEFAULT nextval('diagnosis_types_id_seq'),
    name VARCHAR(255)   NOT NULL,
    code VARCHAR(255)   NOT NULL UNIQUE
);

CREATE TABLE diagnosis_tasks
(
    id                              UUID            NOT NULL PRIMARY KEY,
    created_at                      TIMESTAMP       NOT NULL,
    error_description               TEXT,
    wait_for_appointment_validation BOOLEAN         NOT NULL,
    appointment_id                  UUID,
    original_file_id                VARCHAR(255)    NOT NULL,
    state                           VARCHAR(255)    NOT NULL,
    diagnosis_type_code             VARCHAR(255)    NOT NULL REFERENCES diagnosis_types(code),
    UNIQUE (id, original_file_id)
);

CREATE TABLE checking_services
(
    id                  INTEGER         NOT NULL PRIMARY KEY DEFAULT nextval('checking_service_id_seq'),
    service_name        VARCHAR(255)    NOT NULL,
    diagnosis_type_code VARCHAR(255)    NOT NULL REFERENCES diagnosis_types (code)
);

CREATE TABLE diagnosis_task_results
(
    id                UUID NOT NULL PRIMARY KEY,
    task_id           UUID NOT NULL REFERENCES diagnosis_tasks (id),
    prediction        VARCHAR(255),
    segmented_file_id VARCHAR(255),
    active            BOOLEAN
);

CREATE UNIQUE INDEX uq_diagnosis_task_results_task_id_active on diagnosis_task_results (id ,active) where active = true;

CREATE TABLE diagnosis_tasks_checking_services
(
    checking_service_id BIGINT NOT NULL REFERENCES checking_services(id),
    diagnosis_task_id   UUID   NOT NULL REFERENCES diagnosis_tasks(id),
    CONSTRAINT pk_diagnosis_tasks_checking_services PRIMARY KEY (checking_service_id, diagnosis_task_id)
);

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
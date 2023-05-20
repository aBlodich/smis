CREATE TABLE appointments
(
    id                          uuid        not null primary key,
    patient_id                  uuid        not null,
    doctor_id                   uuid        not null,
    doctor_specialization_id    int         not null,
    appointment_date            timestamp   not null,
    type                        varchar     not null,
    format                      varchar     not null
);

CREATE TABLE appointments_infos
(
    appointment_id  uuid        not null    primary key,
    inspection      text,
    diagnose        text,
    recommendations text
);

CREATE TABLE appointments_infos_attachments
(
    appointment_info_id uuid            not null    references appointments_infos(appointment_id),
    diagnosis_task_id   uuid            not null,
    original_file_id    varchar(255)    not null,
    segmented_file_id   varchar(255),
    primary key (appointment_info_id, diagnosis_task_id)
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
-- liquibase formatted sql

-- changeset dfetisov:1
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='city'
CREATE TABLE city
(
    id          BIGINT PRIMARY KEY generated always as identity,
    city_name   TEXT NOT NULL,
    time_zone   INT,
    is_approved BOOLEAN DEFAULT FALSE
);

-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='volunteer'
CREATE TABLE volunteer
(
    id               BIGINT PRIMARY KEY generated always as identity,
    name             TEXT        NOT NULL,
    phone            VARCHAR(15) NOT NULL,
    city_id          BIGINT      NOT NULL REFERENCES city (id),
    address          TEXT        NOT NULL,
    chat_telegram_id BIGINT      NOT NULL,
    is_work          BOOLEAN DEFAULT FALSE
);

-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='client'
CREATE TABLE client
(
    id               BIGINT PRIMARY KEY generated always as identity,
    name             TEXT        NOT NULL,
    phone            VARCHAR(15) NOT NULL,
    city_id          BIGINT      NOT NULL REFERENCES city (id),
    address          TEXT        NOT NULL,
    chat_telegram_id BIGINT      NOT NULL
);

-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer'
CREATE TABLE request_volunteer
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_client             BIGINT    NOT NULL REFERENCES client (id),
    id_volunteer          BIGINT    NOT NULL REFERENCES volunteer (id),
    is_open               BOOLEAN DEFAULT FALSE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP NOT NULL
);

-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='call_request'
CREATE TABLE call_request
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_client             BIGINT    NOT NULL REFERENCES client (id),
    is_open               BOOLEAN DEFAULT FALSE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP NOT NULL
);

-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='need_finish_request_client'
CREATE TABLE need_finish_request_client
(
    id_client BIGINT REFERENCES client (id),
    command   TEXT NOT NULL
);

-- precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='need_finish_request_volunteer'
CREATE TABLE need_finish_request_volunteer
(
    id_volunteer BIGINT REFERENCES volunteer (id),
    command      TEXT NOT NULL
);



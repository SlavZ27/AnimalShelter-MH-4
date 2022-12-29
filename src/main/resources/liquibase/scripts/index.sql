--liquibase formatted sql

--changeset dfetisov:1
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='city'
--onFail=MARK_RAN
CREATE TABLE city
(
    id          BIGINT PRIMARY KEY generated always as identity,
    city_name   TEXT NOT NULL,
    time_zone   INT check ( time_zone between -11 and +12),
    is_approved BOOLEAN DEFAULT FALSE
);

--changeset dfetisov:2
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='chat'
--onFail=MARK_RAN
CREATE TABLE chat
(
    id               BIGINT PRIMARY KEY generated always as identity,
    name             TEXT   NOT NULL,
    phone            VARCHAR(15),
    city_id          BIGINT REFERENCES city (id),
    address          TEXT,
    is_volunteer     BOOLEAN DEFAULT FALSE
);

--changeset dfetisov:3
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer'
--onFail=MARK_RAN
CREATE TABLE request_volunteer
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_chat_client        BIGINT    NOT NULL REFERENCES chat (id),
    id_chat_volunteer     BIGINT    REFERENCES chat (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);

--changeset dfetisov:4
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='call_request'
--onFail=MARK_RAN
CREATE TABLE call_request
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_chat_client        BIGINT    NOT NULL REFERENCES chat (id),
    id_chat_volunteer     BIGINT    REFERENCES chat (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);

--changeset dfetisov:5
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request'
--onFail=MARK_RAN
CREATE TABLE unfinished_request
(
    id      BIGINT PRIMARY KEY generated always as identity,
    id_chat BIGINT REFERENCES chat (id),
    command TEXT NOT NULL
);
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
    id           BIGINT PRIMARY KEY generated always as identity,
    name         TEXT NOT NULL,
    phone        VARCHAR(15),
    city_id      BIGINT REFERENCES city (id),
    address      TEXT,
    is_volunteer BOOLEAN DEFAULT FALSE
);

--changeset dfetisov:3
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer'
--onFail=MARK_RAN
CREATE TABLE request_volunteer
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_chat_client        BIGINT    NOT NULL REFERENCES chat (id),
    id_chat_volunteer     BIGINT REFERENCES chat (id),
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
    id_chat_volunteer     BIGINT REFERENCES chat (id),
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
--changeset zaytsev:6
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='chat'
--onFail=MARK_RAN
ALTER TABLE chat
    drop column city_id;
--changeset zaytsev:7
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request'
--onFail=MARK_RAN
DROP TABLE city;
--changeset zaytsev:8
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='chat'
--onFail=MARK_RAN
alter table chat
    alter column id drop identity;
--changeset zaytsev:9
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer'
--onFail=MARK_RAN
drop table request_volunteer;
--changeset zaytsev:10
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='call_request'
--onFail=MARK_RAN
drop table call_request;
--changeset zaytsev:11
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request'
--onFail=MARK_RAN
drop table unfinished_request;
--changeset zaytsev:12
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='chat'
--onFail=MARK_RAN
drop table chat;
--changeset zaytsev:13
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='user'
--onFail=MARK_RAN
CREATE TABLE users
(
    id                 BIGINT PRIMARY KEY generated always as identity,
    name_user          TEXT,
    user_name_telegram TEXT,
    id_chat_telegram   TEXT,
    phone              VARCHAR(15),
    address            TEXT,
    is_volunteer       BOOLEAN DEFAULT FALSE
);
--changeset zaytsev:14
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='call_request'
--onFail=MARK_RAN
CREATE TABLE call_request
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_client             BIGINT REFERENCES users (id),
    id_volunteer          BIGINT REFERENCES users (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);
--changeset zaytsev:15
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer'
--onFail=MARK_RAN
CREATE TABLE request_volunteer
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_client             BIGINT    NOT NULL REFERENCES users (id),
    id_volunteer          BIGINT    NOT NULL REFERENCES users (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);
--changeset zaytsev:16
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='telegram_chat'
--onFail=MARK_RAN
CREATE TABLE telegram_chat
(
    id                 BIGINT PRIMARY KEY,
    user_name_telegram TEXT,
    first_name_user    TEXT,
    last_name_user     TEXT,
    last_activity      TIMESTAMP
);
--changeset zaytsev:17
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram'
--onFail=MARK_RAN
alter TABLE users
    drop column user_name_telegram;
alter TABLE users
    drop column id_chat_telegram;
alter TABLE users
    add column id_telegram_chat BIGINT REFERENCES telegram_chat (id);
--changeset zaytsev:18
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram'
--onFail=MARK_RAN
CREATE TABLE unfinished_request_telegram
(
    id               BIGINT PRIMARY KEY,
    id_chat_telegram BIGINT REFERENCES telegram_chat (id),
    command          TEXT NOT NULL
);
--changeset zaytsev:19
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram'
--onFail=MARK_RAN
alter TABLE unfinished_request_telegram
    alter column id add generated always as identity;
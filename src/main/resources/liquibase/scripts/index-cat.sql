--liquibase formatted sql

--changeset dfetisov:1
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='city' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.city
(
    id          BIGINT PRIMARY KEY generated always as identity,
    city_name   TEXT NOT NULL,
    time_zone   INT check ( time_zone between -11 and +12),
    is_approved BOOLEAN DEFAULT FALSE
);

--changeset dfetisov:2
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='chat' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.chat
(
    id           BIGINT PRIMARY KEY generated always as identity,
    name         TEXT NOT NULL,
    phone        VARCHAR(15),
    city_id      BIGINT REFERENCES cat.city (id),
    address      TEXT,
    is_volunteer BOOLEAN DEFAULT FALSE
);

--changeset dfetisov:3
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.request_volunteer
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_chat_client        BIGINT    NOT NULL REFERENCES cat.chat (id),
    id_chat_volunteer     BIGINT REFERENCES cat.chat (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);

--changeset dfetisov:4
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='call_request' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.call_request
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_chat_client        BIGINT    NOT NULL REFERENCES cat.chat (id),
    id_chat_volunteer     BIGINT REFERENCES cat.chat (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);

--changeset dfetisov:5
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.unfinished_request
(
    id      BIGINT PRIMARY KEY generated always as identity,
    id_chat BIGINT REFERENCES cat.chat (id),
    command TEXT NOT NULL
);
--changeset zaytsev:6
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='chat' and schemaname='cat'
--onFail=MARK_RAN
ALTER TABLE cat.chat
    drop column city_id;
--changeset zaytsev:7
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request' and schemaname='cat'
--onFail=MARK_RAN
DROP TABLE cat.city;
--changeset zaytsev:8
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='chat' and schemaname='cat'
--onFail=MARK_RAN
alter table cat.chat
    alter column id drop identity;
--changeset zaytsev:9
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer' and schemaname='cat'
--onFail=MARK_RAN
drop table cat.request_volunteer;
--changeset zaytsev:10
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='call_request' and schemaname='cat'
--onFail=MARK_RAN
drop table cat.call_request;
--changeset zaytsev:11
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request' and schemaname='cat'
--onFail=MARK_RAN
drop table cat.unfinished_request;
--changeset zaytsev:12
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='chat' and schemaname='cat'
--onFail=MARK_RAN
drop table cat.chat;
--changeset zaytsev:13
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='user' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.users
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
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='call_request' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.call_request
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_client             BIGINT REFERENCES cat.users (id),
    id_volunteer          BIGINT REFERENCES cat.users (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);
--changeset zaytsev:15
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.request_volunteer
(
    id                    BIGINT PRIMARY KEY generated always as identity,
    id_client             BIGINT    NOT NULL REFERENCES cat.users (id),
    id_volunteer          BIGINT    NOT NULL REFERENCES cat.users (id),
    is_open               BOOLEAN DEFAULT TRUE,
    local_date_time_open  TIMESTAMP NOT NULL,
    local_date_time_close TIMESTAMP
);
--changeset zaytsev:16
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='telegram_chat' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.telegram_chat
(
    id                 BIGINT PRIMARY KEY,
    user_name_telegram TEXT,
    first_name_user    TEXT,
    last_name_user     TEXT,
    last_activity      TIMESTAMP
);
--changeset zaytsev:17
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.users
    drop column user_name_telegram;
alter TABLE cat.users
    drop column id_chat_telegram;
alter TABLE cat.users
    add column id_telegram_chat BIGINT REFERENCES cat.telegram_chat (id);
--changeset zaytsev:18
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.unfinished_request_telegram
(
    id               BIGINT PRIMARY KEY,
    id_chat_telegram BIGINT REFERENCES cat.telegram_chat (id),
    command          TEXT NOT NULL
);
--changeset zaytsev:19
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.unfinished_request_telegram
    alter column id add generated always as identity;
--changeset zaytsev:20
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.unfinished_request_telegram
    add unique (id_chat_telegram);
--changeset zaytsev:21
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer' and schemaname='cat'
--onFail=MARK_RAN
drop table cat.request_volunteer;
--changeset zaytsev:22
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users' and schemaname='cat'
--onFail=MARK_RAN
alter table cat.users
    add column is_owner boolean default false;
--changeset zaytsev:23
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='animal_type' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.animal_type
(
    id          BIGINT PRIMARY KEY generated always as identity,
    type_animal TEXT NOT NULL
);
--changeset zaytsev:24
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='animal' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.animal
(
    id             BIGINT PRIMARY KEY generated always as identity,
    name_animal    TEXT not null,
    born           DATE,
    id_animal_type BIGINT REFERENCES cat.animal_type (id)
);
--changeset zaytsev:25
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='animal_ownership' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.animal_ownership
(
    id             BIGINT PRIMARY KEY generated always as identity,
    id_user        BIGINT REFERENCES cat.users (id),
    id_animal      BIGINT REFERENCES cat.animal (id),
    date_start_own DATE,
    date_end_trial DATE
);
--changeset zaytsev:26
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='photo' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.photo
(
    id       BIGINT PRIMARY KEY generated always as identity,
    id_media BIGINT
);
--changeset zaytsev:27
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='report' and schemaname='cat'
--onFail=MARK_RAN
CREATE TABLE cat.report
(
    id                  BIGINT PRIMARY KEY generated always as identity,
    id_animal_ownership BIGINT REFERENCES cat.animal_ownership (id),
    report_date         DATE,
    diet                text,
    feeling             text,
    behavior            text,
    id_photo            BIGINT REFERENCES cat.photo (id)
);
--changeset zaytsev:28
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users' and schemaname='cat'
--onFail=MARK_RAN
alter table cat.users
    drop column is_owner;
--changeset zaytsev:29
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='photo' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.photo
    alter column id_media type text;
--changeset zaytsev:30
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='report' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.report
    add column is_approve boolean;
alter TABLE cat.report
    add column is_open boolean default true;
--changeset zaytsev:31
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal_ownership' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.animal_ownership
    add column is_approve boolean;
alter TABLE cat.animal_ownership
    add column is_open boolean default true;
--changeset zaytsev:32
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='report' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.report
    drop column is_open;
--changeset zaytsev:33
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users' and schemaname='cat'
--onFail=MARK_RAN
alter table cat.users
    add column date_last_notification TIMESTAMP;
--changeset zaytsev:34
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='telegram_chat' and schemaname='cat'
--onFail=MARK_RAN
alter table cat.telegram_chat
    add column index_menu int,
    add column shelter    text;
--changeset zaytsev:35
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal' and schemaname='cat'
--onFail=MARK_RAN
alter TABLE cat.animal
    drop column id_animal_type;
--changeset zaytsev:36
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal_type' and schemaname='cat'
--onFail=MARK_RAN
drop table cat.animal_type;
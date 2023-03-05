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
--changeset zaytsev:20
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram'
--onFail=MARK_RAN
alter TABLE unfinished_request_telegram
    add unique (id_chat_telegram);
--changeset zaytsev:21
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='request_volunteer'
--onFail=MARK_RAN
drop table request_volunteer;
--changeset zaytsev:22
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users'
--onFail=MARK_RAN
alter table users
    add column is_owner boolean default false;
--changeset zaytsev:23
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='animal_type'
--onFail=MARK_RAN
CREATE TABLE animal_type
(
    id          BIGINT PRIMARY KEY generated always as identity,
    type_animal TEXT NOT NULL
);
--changeset zaytsev:24
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='animal'
--onFail=MARK_RAN
CREATE TABLE animal
(
    id             BIGINT PRIMARY KEY generated always as identity,
    name_animal    TEXT not null,
    born           DATE,
    id_animal_type BIGINT REFERENCES animal_type (id)
);
--changeset zaytsev:25
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='animal_ownership'
--onFail=MARK_RAN
CREATE TABLE animal_ownership
(
    id             BIGINT PRIMARY KEY generated always as identity,
    id_user        BIGINT REFERENCES users (id),
    id_animal      BIGINT REFERENCES animal (id),
    date_start_own DATE,
    date_end_trial DATE
);
--changeset zaytsev:26
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='photo'
--onFail=MARK_RAN
CREATE TABLE photo
(
    id       BIGINT PRIMARY KEY generated always as identity,
    id_media BIGINT
);
--changeset zaytsev:27
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='report'
--onFail=MARK_RAN
CREATE TABLE report
(
    id                  BIGINT PRIMARY KEY generated always as identity,
    id_animal_ownership BIGINT REFERENCES animal_ownership (id),
    report_date         DATE,
    diet                text,
    feeling             text,
    behavior            text,
    id_photo            BIGINT REFERENCES photo (id)
);
--changeset zaytsev:28
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users'
--onFail=MARK_RAN
alter table users
    drop column is_owner;
--changeset zaytsev:29
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='photo'
--onFail=MARK_RAN
alter TABLE photo
    alter column id_media type text;
--changeset zaytsev:30
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='report'
--onFail=MARK_RAN
alter TABLE report
    add column is_approve boolean;
alter TABLE report
    add column is_open boolean default true;
--changeset zaytsev:31
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal_ownership'
--onFail=MARK_RAN
alter TABLE animal_ownership
    add column is_approve boolean;
alter TABLE animal_ownership
    add column is_open boolean default true;
--changeset zaytsev:32
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='report'
--onFail=MARK_RAN
alter TABLE report
    drop column is_open;
--changeset zaytsev:33
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users'
--onFail=MARK_RAN
alter table users
    add column date_last_notification TIMESTAMP;
--changeset zaytsev:34
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal'
--onFail=MARK_RAN
alter TABLE animal
    drop column id_animal_type;
--changeset zaytsev:35
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal_type'
--onFail=MARK_RAN
drop TABLE animal_type;
--changeset zaytsev:36
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='shelter'
--onFail=MARK_RAN
create TABLE shelter
(
    id               BIGINT PRIMARY KEY generated always as identity,
    text_designation varchar(15),
    name_shelter     TEXT,
    address          text,
    phone            varchar(15)
);
--changeset zaytsev:37
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal'
--onFail=MARK_RAN
alter TABLE animal
    add column id_shelter BIGINT REFERENCES shelter (id);
--changeset zaytsev:38
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='shelter_info'
--onFail=MARK_RAN
create TABLE shelter_info
(
    id               BIGINT PRIMARY KEY generated always as identity,
    id_shelter       BIGINT REFERENCES shelter (id),
    info_designation TEXT,
    text             text
);
--changeset zaytsev:39
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users'
--onFail=MARK_RAN
alter TABLE users
    add column id_shelter BIGINT REFERENCES shelter (id);
--changeset zaytsev:40
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='telegram_chat'
--onFail=MARK_RAN
alter table telegram_chat
    add column index_menu int,
    add column id_shelter BIGINT REFERENCES shelter (id);
--changeset zaytsev:41
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='users'
--onFail=MARK_RAN
alter TABLE users
    alter column id_shelter set NOT NULL;
alter table users
    add CONSTRAINT unique_id_and_shelter_id UNIQUE (id, id_shelter);
--changeset zaytsev:42
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='telegram_chat'
--onFail=MARK_RAN
alter table telegram_chat
    alter column id_shelter set NOT NULL;
--changeset zaytsev:43
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal'
--onFail=MARK_RAN
alter TABLE animal
    alter column id_shelter set NOT NULL;
alter table animal
    add CONSTRAINT animal_unique_id_and_shelter_id UNIQUE (id, id_shelter);
--changeset zaytsev:44
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='animal_ownership'
--onFail=MARK_RAN
alter TABLE animal_ownership
    add column id_shelter BIGINT REFERENCES shelter (id) NOT NULL;
alter table animal_ownership
    add CONSTRAINT animal_ownership_unique_id_and_shelter_id UNIQUE (id, id_shelter);
--changeset zaytsev:45
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='report'
--onFail=MARK_RAN
alter TABLE report
    add column id_shelter BIGINT REFERENCES shelter (id) NOT NULL;
alter table report
    add CONSTRAINT report_unique_id_and_shelter_id UNIQUE (id, id_shelter);
--changeset zaytsev:46
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='photo'
--onFail=MARK_RAN
alter TABLE photo
    add column id_shelter BIGINT REFERENCES shelter (id) NOT NULL;
alter table photo
    add CONSTRAINT photo_unique_id_and_shelter_id UNIQUE (id, id_shelter);
--changeset zaytsev:47
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='call_request'
--onFail=MARK_RAN
alter TABLE call_request
    add column id_shelter BIGINT REFERENCES shelter (id) NOT NULL;
alter table call_request
    add CONSTRAINT call_request_unique_id_and_shelter_id UNIQUE (id, id_shelter);
--changeset zaytsev:48
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='call_request'
--onFail=MARK_RAN
alter table call_request
    add CONSTRAINT unique_id_shelter_id_client_local_date_time_open UNIQUE (id_shelter, id_client, local_date_time_open);
--changeset zaytsev:49
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='telegram_chat'
--onFail=MARK_RAN
alter table telegram_chat
    alter column id_shelter drop not null;
--liquibase formatted sql

--changeset dfetisov:1
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='chat' and schemaname='public'
--onFail=MARK_RAN
CREATE TABLE public.telegram_chat
(
    id                 BIGINT PRIMARY KEY,
    user_name_telegram TEXT,
    first_name_user    TEXT,
    last_name_user     TEXT,
    last_activity      TIMESTAMP
);
--changeset dfetisov:2
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request' and schemaname='public'
--onFail=MARK_RAN
CREATE TABLE public.unfinished_request_telegram
(
    id               BIGINT PRIMARY KEY,
    id_chat_telegram BIGINT REFERENCES public.telegram_chat (id),
    command          TEXT NOT NULL
);
--changeset zaytsev:3
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram' and schemaname='public'
--onFail=MARK_RAN
alter TABLE public.unfinished_request_telegram
    alter column id add generated always as identity;
--changeset zaytsev:4
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='unfinished_request_telegram' and schemaname='public'
--onFail=MARK_RAN
alter TABLE public.unfinished_request_telegram
    add unique (id_chat_telegram);
--changeset zaytsev:5
--precondition-sql-check expectedResult:1 SELECT count(*) FROM pg_tables WHERE tablename='telegram_chat' and schemaname='public'
--onFail=MARK_RAN
alter table public.telegram_chat
    add column index_menu    int,
    add column shelter text;
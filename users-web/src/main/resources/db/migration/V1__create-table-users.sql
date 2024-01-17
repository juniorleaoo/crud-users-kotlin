CREATE TABLE users
(
    id         UUID primary key,
    nick       varchar(32) unique not null,
    birth_date date               not null,
    name       varchar(100)       not null,
    stack      text null
)
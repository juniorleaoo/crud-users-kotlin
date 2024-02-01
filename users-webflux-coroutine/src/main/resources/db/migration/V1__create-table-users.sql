create table users
(
    id         raw(16) not null,
    birth_date timestamp(6) not null,
    nick       varchar2(32 char),
    name       varchar2(255 char) not null unique,
    stack      clob,
    primary key (id)
)

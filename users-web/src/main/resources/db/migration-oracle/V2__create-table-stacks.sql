create table stacks
(
    id      raw(16)           not null,
    name    varchar2(32 char) not null,
    score   number(10, 0)     not null,
    user_id raw(16)           not null,
    primary key (id),
    constraint uk_stacks_user_id_name unique (user_id, name),
    constraint fk_users_stacks foreign key (user_id) references users
)


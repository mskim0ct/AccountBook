create table users
(
    id               bigint auto_increment
        primary key,
    authority        varchar(255) null,
    created_at       datetime(6)  not null,
    email            varchar(255) not null,
    password         varchar(255) not null,
    login_expired_at datetime(6)  null
);

create table account_records
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)  not null,
    deleted    bit          not null,
    memo       varchar(255) null,
    money      int          not null,
    type       varchar(255) not null,
    updated_at datetime(6)  not null,
    used_at    date         not null,
    user_id    bigint       not null,
    constraint FKb984s8qnbrboun7u0fu3yr3a9
        foreign key (user_id) references users (id)
);


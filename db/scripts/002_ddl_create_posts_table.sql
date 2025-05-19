create table posts
(
    id          serial primary key,
    description varchar not null,
    created     timestamp,
    user_id     int references users(id)
);
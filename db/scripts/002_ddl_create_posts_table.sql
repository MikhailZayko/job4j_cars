CREATE TABLE posts
(
    id          SERIAL PRIMARY KEY,
    description VARCHAR NOT NULL,
    created     TIMESTAMP,
    user_id     INT REFERENCES users(id)
);
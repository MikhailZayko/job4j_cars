CREATE TABLE owners
(
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    user_id INT NOT NULL REFERENCES users(id)
);
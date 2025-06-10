CREATE TABLE history_owner
(
    id SERIAL PRIMARY KEY,
    car_id INT NOT NULL REFERENCES car(id),
    owner_id INT NOT NULL REFERENCES owners(id),
    UNIQUE (car_id, owner_id)
);
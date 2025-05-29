CREATE TABLE price_history (
    id SERIAL PRIMARY KEY,
    before BIGINT NOT NULL,
    after BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    post_id INT NOT NULL,
    CONSTRAINT fk_price_history_post FOREIGN KEY (post_id) REFERENCES posts(id)
);

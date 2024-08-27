CREATE TABLE users
(
    id        SERIAL PRIMARY KEY,
    username  VARCHAR(50) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username) VALUES ('alice');
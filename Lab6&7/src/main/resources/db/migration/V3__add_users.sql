-- Adds user accounts table for JWT authentication.

CREATE TABLE users (
    username VARCHAR(50)  PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE
);

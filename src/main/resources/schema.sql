CREATE TABLE IF NOT EXISTS users (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR NOT NULL UNIQUE,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    owner_id INT NOT NULL,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    available BOOLEAN,
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status VARCHAR NOT NULL,
    item_id INT NOT NULL,
    user_id INT NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    owner_id INT NOT NULL,
    item_id INT NOT NULL,
    text VARCHAR NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE
);

--DROP TABLE IF EXISTS users CASCADE;
--DROP TABLE IF EXISTS items CASCADE;
--
--CREATE TABLE IF NOT EXISTS users (
--  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
--  name VARCHAR(255) NOT NULL,
--  email VARCHAR(512) NOT NULL,
--  CONSTRAINT pk_user PRIMARY KEY (id),
--  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
--);
--
--CREATE TABLE IF NOT EXISTS items (
--  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
--  owner_id INT NOT NULL,
--  name VARCHAR(255) NOT NULL,
--  description VARCHAR NOT NULL,
--  available BOOLEAN,
--  CONSTRAINT pk_item PRIMARY KEY (id),
--  FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
--);
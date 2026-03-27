DROP TABLE IF EXISTS genre_inheritance;
DROP TABLE IF EXISTS characters;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS actors;
DROP TABLE IF EXISTS genres;

CREATE TABLE genres (
        id SERIAL PRIMARY KEY,
        name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE genre_inheritance (
        parent_id INT REFERENCES genres(id),
        child_id INT REFERENCES genres(id),
        PRIMARY KEY (parent_id, child_id)
);

CREATE TABLE movies (
        id SERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        release_date DATE,
        duration TIME,
        score DECIMAL(10, 0),
        genre_id INT NOT NULL REFERENCES genres(id)
);


CREATE TABLE actors (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL
);


CREATE TABLE characters (
        id SERIAL PRIMARY KEY,
        movie_id INT NOT NULL REFERENCES movies(id),
        actor_id INT REFERENCES actors(id),
        name VARCHAR(255) NOT NULL
);
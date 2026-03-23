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
        score DECIMAL(10, 1),
        genre_id INT REFERENCES genres(id)
);


CREATE TABLE actors (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) UNIQUE NOT NULL
);


CREATE TABLE movie_actors (
        movie_id INT REFERENCES movies(id),
        actor_id INT REFERENCES actors(id),
        PRIMARY KEY (movie_id, actor_id)
);
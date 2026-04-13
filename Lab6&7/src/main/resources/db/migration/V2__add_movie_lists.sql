-- Adds movie list support: a named list with a creation timestamp that
-- groups an arbitrary collection of movies via a junction table.

CREATE TABLE movie_lists (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movie_list_movies (
    list_id  INT NOT NULL REFERENCES movie_lists(id)  ON DELETE CASCADE,
    movie_id INT NOT NULL REFERENCES movies(id)        ON DELETE CASCADE,
    PRIMARY KEY (list_id, movie_id)
);

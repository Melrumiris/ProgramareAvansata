DELETE FROM characters;
DELETE FROM actors;
DELETE FROM movies;
DELETE FROM genres;

INSERT INTO genres (name) VALUES
                              ('Action'),
                              ('Drama'),
                              ('Sci-Fi'),
                              ('Comedy');

-- Actors
INSERT INTO actors (name) VALUES
                              ('Ava Summers'),
                              ('Miles Thatcher'),
                              ('Selene Park'),
                              ('Jonas Price');

-- Movies
INSERT INTO movies (title, release_date, duration, score, genre_id)
VALUES
    ('Solaris Dawn', '2022-05-18', '01:38:00', 8, (SELECT id FROM genres WHERE name = 'Sci-Fi')),
    ('Glass Harbor', '2021-11-02', '02:02:00', 9, (SELECT id FROM genres WHERE name = 'Drama')),
    ('Quick Circuit', '2023-03-14', '01:24:30', 7, (SELECT id FROM genres WHERE name = 'Action')),
    ('Late Night Cipher', '2024-07-30', '01:47:15', 8, (SELECT id FROM genres WHERE name = 'Comedy'));

-- Characters linking actors to movies (assumes movie IDs exist)
INSERT INTO characters (movie_id, actor_id, name)
VALUES
    ((SELECT id FROM movies WHERE title = 'Solaris Dawn'), (SELECT id FROM actors WHERE name = 'Selene Park'), 'Commander Imani Rhodes'),
    ((SELECT id FROM movies WHERE title = 'Solaris Dawn'), (SELECT id FROM actors WHERE name = 'Miles Thatcher'), 'Dr. Cai Montes'),
    ((SELECT id FROM movies WHERE title = 'Glass Harbor'), (SELECT id FROM actors WHERE name = 'Jonas Price'), 'Detective Leo Warren'),
    ((SELECT id FROM movies WHERE title = 'Quick Circuit'), (SELECT id FROM actors WHERE name = 'Ava Summers'), 'Pilot Reina Flores'),
    ((SELECT id FROM movies WHERE title = 'Late Night Cipher'), (SELECT id FROM actors WHERE name = 'Ava Summers'), 'Producer Zara Lane');

COMMIT;


DELETE
FROM users;
DELETE
FROM films;
DELETE
FROM likes;
DELETE
FROM genres;
DELETE
FROM mpa;
DELETE
FROM reviews;
DELETE
FROM reviews_likes;

ALTER TABLE users
    ALTER COLUMN user_id RESTART WITH 1;

ALTER TABLE genres
    ALTER COLUMN genre_id RESTART WITH 1;

ALTER TABLE reviews
    ALTER COLUMN review_id RESTART WITH 1;

INSERT INTO genres (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

ALTER TABLE mpa
    ALTER COLUMN mpa_id RESTART WITH 1;

INSERT INTO mpa (name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

ALTER TABLE films
    ALTER COLUMN film_id RESTART WITH 1;

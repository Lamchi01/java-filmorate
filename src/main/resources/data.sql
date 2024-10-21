INSERT INTO GENRE (NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO MPA_RATING (NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
VALUES ('Фильм 1', 'Описание фильма 1', '2022-01-01', 120, 1),
       ('Фильм 2', 'Описание фильма 2', '2022-01-01', 120, 2),
       ('Фильм 3', 'Описание фильма 3', '2022-01-01', 120, 3),
       ('Фильм 4', 'Описание фильма 4', '2022-01-01', 120, 4),
       ('Фильм 5', 'Описание фильма 5', '2022-01-01', 120, 5);

INSERT INTO GENRES (FILM_ID, ID)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);

INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES ('L0kDZ@example.com', 'login1', 'name1', '2022-01-01'),
       ('Y2ZG8@example.com', 'login2', 'name2', '2022-01-01'),
       ('P8wF0@example.com', 'login3', 'name3', '2022-01-01'),
       ('pG2Ow@example.com', 'login4', 'name4', '2022-01-01'),
       ('pPn9k@example.com', 'login5', 'name5', '2022-01-01');

INSERT INTO FRIENDS (USER_ID, FRIEND_ID)
VALUES (1, 2),
       (1, 3),
       (2, 3);

INSERT INTO LIKES (FILM_ID, USER_ID)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 3),
       (3, 4);
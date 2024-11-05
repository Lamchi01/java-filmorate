CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id BIGINT AUTO_INCREMENT,
    name   VARCHAR,
    CONSTRAINT mpa_pk
        PRIMARY KEY (mpa_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id BIGINT AUTO_INCREMENT,
    name     VARCHAR,
    CONSTRAINT genres_PK
        PRIMARY KEY (genre_id)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT AUTO_INCREMENT,
    name         VARCHAR,
    description  VARCHAR(200),
    release_date DATE   NOT NULL,
    duration     BIGINT NOT NULL,
    mpa_id       BIGINT NOT NULL,
    count_likes  BIGINT DEFAULT 0,
    CONSTRAINT films_ps
        PRIMARY KEY (film_id),
    CONSTRAINT films_mpa_FK
        FOREIGN KEY (mpa_id) REFERENCES mpa
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    CONSTRAINT film_genres_films_fk
        FOREIGN KEY (film_id) REFERENCES films ON DELETE CASCADE,
    CONSTRAINT film_genres_genres_fk
        FOREIGN KEY (genre_id) REFERENCES genres ON DELETE CASCADE,
    CONSTRAINT film_genres_PK
        PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT AUTO_INCREMENT,
    email    VARCHAR NOT NULL,
    LOGIN    VARCHAR NOT NULL,
    name     VARCHAR,
    birthday DATE    NOT NULL,
    CONSTRAINT users_pk
        PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    CONSTRAINT friends_users_fk_1
        FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    CONSTRAINT friends_users_fk_2
        FOREIGN KEY (friend_id) REFERENCES users ON DELETE CASCADE,
    CONSTRAINT friends_pk
        PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT likes_films_fk
        FOREIGN KEY (film_id) REFERENCES films ON DELETE CASCADE,
    CONSTRAINT likes_users_fk
        FOREIGN KEY (user_id) REFERENCES users ON DELETE CASCADE,
    CONSTRAINT likes_pk
        PRIMARY KEY (film_id, user_id)
);

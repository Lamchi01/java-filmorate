CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    EMAIL    VARCHAR                                              NOT NULL,
    LOGIN    VARCHAR UNIQUE                                       NOT NULL,
    NAME     VARCHAR,
    BIRTHDAY DATE                                                 NOT NULL
);

CREATE TABLE IF NOT EXISTS GENRE
(
    ID   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    NAME VARCHAR                                          NOT NULL
);

CREATE TABLE IF NOT EXISTS MPA_RATING
(
    MPA_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    NAME   VARCHAR                                              NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS
(
    FILM_ID      INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    NAME         VARCHAR(255)                                     NOT NULL,
    DESCRIPTION  VARCHAR(200)                                     NOT NULL,
    RELEASE_DATE DATE                                             NOT NULL,
    DURATION     INTEGER                                          NOT NULL,
    MPA_ID       INTEGER                                          NOT NULL
        REFERENCES MPA_RATING (MPA_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LIKES
(
    FILM_ID INTEGER REFERENCES FILMS (FILM_ID) ON DELETE CASCADE NOT NULL,
    USER_ID INTEGER REFERENCES USERS (USER_ID) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (FILM_ID, USER_ID)
);

CREATE TABLE IF NOT EXISTS GENRES
(
    FILM_ID INTEGER REFERENCES FILMS (FILM_ID) ON DELETE CASCADE NOT NULL,
    ID      INTEGER REFERENCES GENRE (ID) ON DELETE CASCADE      NOT NULL,
    PRIMARY KEY (FILM_ID, ID)
);

CREATE TABLE IF NOT EXISTS FRIENDS
(
    USER_ID   INTEGER REFERENCES USERS (USER_ID) ON DELETE CASCADE NOT NULL,
    FRIEND_ID INTEGER REFERENCES USERS (USER_ID) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (USER_ID, FRIEND_ID)
);
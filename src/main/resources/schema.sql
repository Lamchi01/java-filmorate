create table if not exists MPA
(
    MPA_ID BIGINT auto_increment,
    NAME   VARCHAR,
    constraint MPA_PK
        primary key (MPA_ID)
);

create table if not exists GENRES
(
    GENRE_ID BIGINT auto_increment,
    NAME     VARCHAR,
    constraint GENRES_PK
        primary key (GENRE_ID)
);

create table if not exists FILMS
(
    FILM_ID      BIGINT auto_increment,
    NAME         VARCHAR,
    DESCRIPTION  VARCHAR(200),
    RELEASE_DATE DATE   not null,
    DURATION     BIGINT not null,
    MPA_ID       BIGINT not null,
    COUNT_LIKES  BIGINT DEFAULT 0,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_MPA_FK
        foreign key (MPA_ID) references MPA
);

create table if not exists FILM_GENRES
(
    FILM_ID  BIGINT not null,
    GENRE_ID BIGINT not null,
    constraint FILM_GENRES_FILMS_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint FILM_GENRES_GENRES_FK
        foreign key (GENRE_ID) references GENRES ON DELETE CASCADE,
    constraint FILM_GENRES_PK
        primary key (FILM_ID, GENRE_ID)
);

create table if not exists USERS
(
    USER_ID  BIGINT auto_increment,
    EMAIL    VARCHAR not null,
    LOGIN    VARCHAR not null,
    NAME     VARCHAR,
    BIRTHDAY DATE    not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table if not exists FRIENDS
(
    USER_ID   BIGINT not null,
    FRIEND_ID BIGINT not null,
    constraint FRIENDS_USERS_FK_1
        foreign key (USER_ID) references USERS ON DELETE CASCADE,
    constraint FRIENDS_USERS_FK_2
        foreign key (FRIEND_ID) references USERS ON DELETE CASCADE,
    constraint FRIENDS_PK
        primary key (USER_ID, FRIEND_ID)
);

create table if not exists LIKES
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint LIKES_FILMS_FK
        foreign key (FILM_ID) references FILMS ON DELETE CASCADE,
    constraint LIKES_USERS_FK
        foreign key (USER_ID) references USERS ON DELETE CASCADE,
    constraint LIKES_PK
        primary key (FILM_ID, USER_ID)
);

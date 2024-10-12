create table if not exists RATINGS_MPA
(
    RATING_MPA_ID BIGINT auto_increment,
    NAME          CHARACTER(255),
    constraint RATINGS_MPA_PK
        primary key (RATING_MPA_ID)
);

create table if not exists GENRES
(
    GENRE_ID BIGINT auto_increment,
    NAME     CHARACTER(255),
    constraint GENRES_PK
        primary key (GENRE_ID)
);

create table if not exists FILMS
(
    FILM_ID       BIGINT auto_increment,
    NAME          CHARACTER(255),
    DESCRIPTION   CHARACTER(200),
    RELEASE_DATE  DATE   not null,
    DURATION      BIGINT not null,
    RATING_MPA_ID BIGINT not null,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS_RATINGS_MPA_FK
        foreign key (RATING_MPA_ID) references RATINGS_MPA
);

create table if not exists FILM_GENRES
(
    FILM_ID  BIGINT not null,
    GENRE_ID BIGINT not null,
    constraint FILM_GENRES_FILMS_FK
        foreign key (FILM_ID) references FILMS,
    constraint FILM_GENRES_GENRES_FK
        foreign key (GENRE_ID) references GENRES
);

create table if not exists USERS
(
    USER_ID  BIGINT auto_increment,
    EMAIL    CHARACTER(255),
    LOGIN    CHARACTER(255),
    NAME     CHARACTER(255),
    BIRTHDAY DATE   not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table if not exists FRIENDS
(
    USER_ID   BIGINT  not null,
    FRIEND_ID BIGINT  not null,
    CONFIRMED BOOLEAN not null,
    constraint FRIENDS_USERS_FK_1
        foreign key (USER_ID) references USERS,
    constraint FRIENDS_USERS_FK_2
        foreign key (FRIEND_ID) references USERS
);

create table if not exists LIKES
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint LIKES_FILMS_FK
        foreign key (FILM_ID) references FILMS,
    constraint LIKES_USERS_FK
        foreign key (USER_ID) references USERS
);

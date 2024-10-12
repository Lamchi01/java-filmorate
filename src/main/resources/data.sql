insert into users (email, login, name, birthday)
    values ('user1@mail.ru', 'user1', 'user name 1', '2001-01-01');
insert into users (email, login, name, birthday)
    values ('user2@mail.ru', 'user2', 'user name 2', '2002-01-01');
insert into users (email, login, name, birthday)
    values ('user3@mail.ru', 'user3', 'user name 3', '2003-01-01');
insert into users (email, login, name, birthday)
    values ('user4@mail.ru', 'user4', 'user name 4', '2004-01-01');
insert into users (email, login, name, birthday)
    values ('user5@mail.ru', 'user5', 'user name 5', '2005-01-01');
insert into users (email, login, name, birthday)
    values ('user6@mail.ru', 'user6', 'user name 6', '2006-01-01');
insert into users (email, login, name, birthday)
    values ('user7@mail.ru', 'user7', 'user name 7', '2007-01-01');

insert into genres (name) values ('genre 1');
insert into genres (name) values ('genre 2');
insert into genres (name) values ('genre 3');
insert into genres (name) values ('genre 4');
insert into genres (name) values ('genre 5');

insert into ratings_mpa (name) values ('rating mpa 1');
insert into ratings_mpa (name) values ('rating mpa 2');
insert into ratings_mpa (name) values ('rating mpa 3');
insert into ratings_mpa (name) values ('rating mpa 4');
insert into ratings_mpa (name) values ('rating mpa 5');

insert into films (name, description, release_date, duration, rating_mpa_id)
    values ('film 1', 'film description 1', '1990-01-01', 101, 1);
insert into films (name, description, release_date, duration, rating_mpa_id)
    values ('film 2', 'film description 2', '1990-01-02', 102, 1);
insert into films (name, description, release_date, duration, rating_mpa_id)
    values ('film 3', 'film description 3', '1990-01-03', 103, 2);
insert into films (name, description, release_date, duration, rating_mpa_id)
    values ('film 4', 'film description 4', '1990-01-04', 104, 2);
insert into films (name, description, release_date, duration, rating_mpa_id)
    values ('film 5', 'film description 5', '1990-01-05', 105, 3);
insert into films (name, description, release_date, duration, rating_mpa_id)
    values ('film 6', 'film description 6', '1990-01-06', 106, 4);
insert into films (name, description, release_date, duration, rating_mpa_id)
    values ('film 7', 'film description 7', '1990-01-07', 107, 5);

insert into likes (film_id, user_id) values (1, 1);
insert into likes (film_id, user_id) values (2, 1);
insert into likes (film_id, user_id) values (3, 1);
insert into likes (film_id, user_id) values (4, 1);
insert into likes (film_id, user_id) values (1, 2);
insert into likes (film_id, user_id) values (2, 2);
insert into likes (film_id, user_id) values (1, 3);
insert into likes (film_id, user_id) values (1, 5);
insert into likes (film_id, user_id) values (2, 5);
insert into likes (film_id, user_id) values (1, 6);
insert into likes (film_id, user_id) values (7, 6);

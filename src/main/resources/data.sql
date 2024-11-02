delete
from users;
delete
from films;
delete
from likes;
delete
from genres;
delete
from mpa;

alter table users
    alter column user_id restart with 1;

alter table genres
    alter column genre_id restart with 1;

insert into genres (name)
values ('Комедия');
insert into genres (name)
values ('Драма');
insert into genres (name)
values ('Мультфильм');
insert into genres (name)
values ('Триллер');
insert into genres (name)
values ('Документальный');
insert into genres (name)
values ('Боевик');

alter table mpa
    alter column mpa_id restart with 1;

insert into mpa (name)
values ('G');
insert into mpa (name)
values ('PG');
insert into mpa (name)
values ('PG-13');
insert into mpa (name)
values ('R');
insert into mpa (name)
values ('NC-17');

alter table films
    alter column film_id restart with 1;

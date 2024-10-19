package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface LikeStorage {
    void likeFilm(Film film, User user);

    void deleteLike(Film film, User User);

    long getLikes(Film film);
}

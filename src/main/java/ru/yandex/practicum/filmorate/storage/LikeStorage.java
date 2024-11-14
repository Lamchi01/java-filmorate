package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public interface LikeStorage {
    void likeFilm(Film film, User user);

    void deleteLike(Film film, User user);

    long getLikes(Film film);

    void updateCountLikes(Film film);
}

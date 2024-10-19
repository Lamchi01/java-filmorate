package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {
    void likeFilm(long filmId, long userId);

    void deleteLike(long filmId, long userId);
}

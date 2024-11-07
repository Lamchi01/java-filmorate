package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends BaseStorage<Film> {
    List<Film> popularFilms(int count);

    List<Film> findFilmsByDirectorId(long directorId, String sortedBy);

    List<Film> findCommonFilms(long userId, long friendId);
}


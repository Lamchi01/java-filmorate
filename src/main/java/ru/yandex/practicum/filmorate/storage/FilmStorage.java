package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends BaseStorage<Film> {

    List<Film> getPopularFilms(int count, Long genreId, Integer year);


    List<Film> findFilmsByDirectorId(long directorId, String sortedBy);

    List<Film> findCommonFilms(long userId, long friendId);

    List<Film> findFilms(String query, String by);
}


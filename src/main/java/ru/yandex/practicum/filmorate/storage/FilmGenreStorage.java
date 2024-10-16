package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    List<Genre> getGenres(long filmId);

    void addGenre(long filmId, long genreId);

    void deleteFilmGenres(long filmId);
}

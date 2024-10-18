package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface FilmGenreStorage {
    List<Genre> getGenres(long filmId);

    void addGenre(long filmId, long genreId);

    void addGenres(long filmId, List<Long> genresId);

    void deleteFilmGenres(long filmId);

    Map<Long, LinkedHashSet<Genre>> getAllFilmGenres();
}

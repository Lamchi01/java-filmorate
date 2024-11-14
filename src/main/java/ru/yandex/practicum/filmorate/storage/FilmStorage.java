package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface FilmStorage extends BaseStorage<Film> {

    List<Film> getPopularFilms(int count, Long genreId, Integer year);


    List<Film> findFilmsByDirectorId(long directorId, String sortedBy);

    List<Film> findCommonFilms(long userId, long friendId);

    List<Film> findFilms(String query, String by);

    List<Film> getRecommendation(long id);

    Map<Long, LinkedHashSet<Genre>> getFilmsGenres(List<Film> films);

    Map<Long, LinkedHashSet<Genre>> getAllFilmGenres();

    Map<Long, LinkedHashSet<Director>> getAllFilmDirectors();

    Map<Long, LinkedHashSet<Director>> getFilmsDirectors(List<Film> films);
}


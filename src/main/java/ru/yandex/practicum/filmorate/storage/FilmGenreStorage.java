package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    List<Genre> getGenres(Film film);

    void addGenre(Film film, Genre genre);

    void addGenres(Film film, List<Genre> genres);

    void deleteFilmGenres(Film film);

}

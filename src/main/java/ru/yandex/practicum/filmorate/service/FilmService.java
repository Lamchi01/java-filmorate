package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.WrongRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final BaseStorage<User> userStorage;
    private final BaseStorage<Mpa> mpaStorage;
    private final BaseStorage<Genre> genreStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        Film film = filmStorage.findById(id);
        if (film.getMpa() != null) {
            Mpa mpa = mpaStorage.findById(film.getMpa().getId());
            film.setMpa(mpa);
        }
        List<Genre> genre = filmGenreStorage.getGenres(id);
        film.setGenres(genre);
        return film;
    }

    public void create(Film film) {
        try {
            Mpa mpa = mpaStorage.findById(film.getMpa().getId());
            film.setMpa(mpa);
            filmStorage.create(film);
        } catch (NotFoundException ex) {
            throw new WrongRequestException(ex.getMessage());
        }
        List<Genre> genre = film.getGenres();

        // сначала удалим все жанры фильмы из таблицы FILM_GENRES
        filmGenreStorage.deleteFilmGenres(film.getId());
        if (genre != null && !genre.isEmpty()) {
            try {
                film.setGenres(genre.stream()
                        .map(g -> genreStorage.findById(g.getId()))
                        .distinct()
                        .peek(g -> filmGenreStorage.addGenre(film.getId(), g.getId()))
                        .toList());
            } catch (NotFoundException ex) {
                throw new ValidationException("Не верный ID Genre");
            }
        }
    }

    public Film update(Film film) {
        Film savedFilm = filmStorage.findById(film.getId());

        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getDescription() != null) savedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) savedFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != null) savedFilm.setDuration(film.getDuration());
        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getGenres() != null) {
            filmGenreStorage.deleteFilmGenres(film.getId()); // удалим все жанры фильмы из таблицы FILM_GENRES
            savedFilm.setGenres(film.getGenres());
        }
        if (film.getMpa() != null) savedFilm.setMpa(film.getMpa());
        if (film.getLikes() != null) savedFilm.setLikes(film.getLikes());

        filmStorage.update(film);
        return savedFilm;
    }

    public void deleteAllFilms() {
        filmStorage.deleteAll();
    }

    public Film likeFilm(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        likeStorage.likeFilm(filmId, userId);
        log.trace("Добавлен лайк к фильму с ID: {} пользователем с ID: {}", filmId, userId);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        likeStorage.deleteLike(filmId, userId);
        log.trace("Удален лайк к фильму с ID: {} пользователя с ID: {}", filmId, userId);
        return film;
    }

    public List<Film> popularFilms(int count) {
        log.trace("Получен запрос на получение {} популярных фильмов", count);
        return filmStorage.popularFilms(count);
    }
}

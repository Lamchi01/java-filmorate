package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final BaseStorage<User> userStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final BaseStorage<Mpa> mpaStorage;

    /**
     * Поиск всех фильмов с маппингом жанров
     * (используется продуктивная выборка с HashMap)
     *
     * @return - список фильмов
     */
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        Film film = filmStorage.findById(id);
        film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
        return film;
    }

    public void create(Film film) {
        filmStorage.create(film);
        film.setMpa(mpaStorage.findById(film.getMpa().getId()));

        // сначала удалим все жанры фильмы из таблицы FILM_GENRES
        filmGenreStorage.deleteFilmGenres(film);

        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            filmGenreStorage.addGenres(film, genres.stream().toList());
            film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
        }
    }

    public Film update(Film film) {
        Film savedFilm = filmStorage.findById(film.getId());
        filmStorage.create(film);
        film.setMpa(mpaStorage.findById(film.getMpa().getId()));

        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getDescription() != null) savedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) savedFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != null) savedFilm.setDuration(film.getDuration());
        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getGenres() != null) {
            filmGenreStorage.deleteFilmGenres(film); // удалим все жанры фильмы из таблицы FILM_GENRES
            filmGenreStorage.addGenres(film, film.getGenres().stream().toList());
            savedFilm.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
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
        User user = userStorage.findById(userId);
        likeStorage.likeFilm(film, user);
        log.trace("Добавлен лайк к фильму с ID: {} пользователем с ID: {}", filmId, userId);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likeStorage.deleteLike(film, user);
        log.trace("Удален лайк к фильму с ID: {} пользователя с ID: {}", filmId, userId);
        return film;
    }

    public List<Film> popularFilms(int count) {
        log.trace("Получен запрос на получение {} популярных фильмов", count);
        List<Film> films = filmStorage.popularFilms(count);
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
        }
        return films;
    }
}

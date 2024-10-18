package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final BaseStorage<User> userStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;

    /**
     * Поиск всех фильмов с маппингом жанров
     * (используется продуктивная выборка с HashMap)
     *
     * @return - список фильмов
     */
    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        Map<Long, LinkedHashSet<Genre>> genres = filmGenreStorage.getAllFilmGenres();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(new LinkedHashSet<>(genres.get(film.getId())));
            }
        }
        return films;
    }

    public Film findById(Long id) {
        Film film = filmStorage.findById(id);
        film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(id)));
        return film;
    }

    public void create(Film film) {
        filmStorage.create(film);
        Set<Genre> genres = film.getGenres();

        // сначала удалим все жанры фильмы из таблицы FILM_GENRES
        filmGenreStorage.deleteFilmGenres(film.getId());

        if (genres != null && !genres.isEmpty()) {
            filmGenreStorage.addGenres(film.getId(), genres.stream().map(Genre::getId).toList());
            film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film.getId())));
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
            filmGenreStorage.addGenres(film.getId(), film.getGenres().stream().map(Genre::getId).toList());
            savedFilm.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film.getId())));
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
        List<Film> films = filmStorage.popularFilms(count);
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film.getId())));
        }
        return films;
    }
}

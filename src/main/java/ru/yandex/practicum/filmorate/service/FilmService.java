package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.yandex.practicum.filmorate.model.Event.EventType.LIKE;
import static ru.yandex.practicum.filmorate.model.Event.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Event.Operation.REMOVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final BaseStorage<User> userStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final DirectorStorage directorStorage;
    private final BaseStorage<Mpa> mpaStorage;
    private final EventStorage eventStorage;

    /**
     * Поиск всех фильмов с маппингом жанров
     * (используется продуктивная выборка с HashMap)
     *
     * @return - список фильмов
     */
    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        Map<Long, LinkedHashSet<Genre>> genres = filmStorage.getAllFilmGenres();
        Map<Long, LinkedHashSet<Director>> directors = filmStorage.getAllFilmDirectors();
        fillGenresAndDirectorsForFilms(films, genres, directors);
        log.info("Обработан запрос на получение всех фильмов");
        return films;
    }

    public Film findById(Long id) {
        Film film = filmStorage.findById(id);
        film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
        film.setDirectors(new LinkedHashSet<>(directorStorage.getDirectors(film)));
        log.info("Обработан запрос на получение фильма с ID {}", id);
        return film;
    }

    public void create(Film film) {
        filmStorage.create(film);
        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            filmGenreStorage.addGenres(film, genres.stream().toList());
            // оставить для MockMvc тестов контроллеров, так как жанры могут приходить без имени
            film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
        }
        Set<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            directorStorage.addDirectors(film, directors.stream().toList());
            film.setDirectors(new LinkedHashSet<>(directorStorage.getDirectors(film)));
        }
        log.info("Создан фильм с ID {}", film.getId());
    }

    public Film update(Film film) {
        Film savedFilm = filmStorage.findById(film.getId());
        film.setMpa(mpaStorage.findById(film.getMpa().getId()));

        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getDescription() != null) savedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) savedFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != null) savedFilm.setDuration(film.getDuration());
        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getGenres() != null) {
            filmGenreStorage.deleteFilmGenres(savedFilm); // удалим все жанры фильмы из таблицы FILM_GENRES
            filmGenreStorage.addGenres(savedFilm, film.getGenres().stream().toList());
            // оставить для MockMvc тестов контроллеров, так как жанры могут приходить без имени
            savedFilm.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
        }
        if (film.getDirectors() != null) {
            directorStorage.deleteFilmDirectors(savedFilm);
            directorStorage.addDirectors(savedFilm, film.getDirectors().stream().toList());
            savedFilm.setDirectors(new LinkedHashSet<>(directorStorage.getDirectors(film)));
        }
        if (film.getMpa() != null) savedFilm.setMpa(film.getMpa());
        if (film.getLikes() != null) savedFilm.setLikes(film.getLikes());

        filmStorage.update(savedFilm);
        log.info("Обновлен фильм с ID {}", savedFilm.getId());
        return savedFilm;
    }

    public void deleteAllFilms() {
        filmStorage.deleteAll();
        log.info("Удалены все фильмы");
    }

    public Film likeFilm(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likeStorage.likeFilm(film, user);
        log.info("Добавлен лайк к фильму с ID: {} пользователем с ID: {}", filmId, userId);
        likeStorage.updateCountLikes(film);
        eventStorage.addEvent(userId, LIKE, ADD, filmId);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likeStorage.deleteLike(film, user);
        log.info("Удален лайк к фильму с ID: {} пользователя с ID: {}", filmId, userId);
        likeStorage.updateCountLikes(film);
        eventStorage.addEvent(userId, LIKE, REMOVE, filmId);
        return film;
    }

    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        List<Film> films = filmStorage.getPopularFilms(count, genreId, year);
        Map<Long, LinkedHashSet<Genre>> genres = filmStorage.getFilmsGenres(films);
        Map<Long, LinkedHashSet<Director>> directors = filmStorage.getFilmsDirectors(films);
        fillGenresAndDirectorsForFilms(films, genres, directors);
        log.info("Обработан запрос на получение {} популярных фильмов с фильтрацией по жанру и году", count);
        return films;
    }

    public List<Film> findFilmsByDirectorId(long directorId, String sortedBy) {
        directorStorage.findById(directorId);
        List<Film> directorFilms = filmStorage.findFilmsByDirectorId(directorId, sortedBy);
        Map<Long, LinkedHashSet<Genre>> genres = filmStorage.getAllFilmGenres();
        Map<Long, LinkedHashSet<Director>> directors = filmStorage.getAllFilmDirectors();
        fillGenresAndDirectorsForFilms(directorFilms, genres, directors);
        log.info("Обработан запрос на получение фильмов режиссёра с ID = {}", directorId);
        return directorFilms;
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteById(id);
        log.info("Фильм с ID: {} успешно удалён", id);
    }

    public List<Film> findCommonFilms(long userId, long friendId) {
        List<Film> films = filmStorage.findCommonFilms(userId, friendId);
        Map<Long, LinkedHashSet<Genre>> genres = filmStorage.getFilmsGenres(films);
        Map<Long, LinkedHashSet<Director>> directors = filmStorage.getFilmsDirectors(films);
        fillGenresAndDirectorsForFilms(films, genres, directors);
        log.info("Обработан запрос на получение общих фильмов пользователей с ID {} и ID {}", userId, friendId);
        return films;
    }

    public List<Film> findFilms(String query, String by) {
        log.info("Получен запрос на поиск фильмов. Строка поиска = {}", query);
        List<Film> films = filmStorage.findFilms(query, by);
        Map<Long, LinkedHashSet<Genre>> genres = filmStorage.getFilmsGenres(films);
        Map<Long, LinkedHashSet<Director>> directors = filmStorage.getFilmsDirectors(films);
        fillGenresAndDirectorsForFilms(films, genres, directors);
        log.info("Обработан запрос на получение фильмов по запросу {}, {}", query, by);
        return films;
    }

    public List<Film> getRecommendation(long userId) {
        userStorage.findById(userId);
        List<Film> films = filmStorage.getRecommendation(userId);
        Map<Long, LinkedHashSet<Genre>> genres = filmStorage.getFilmsGenres(films);
        Map<Long, LinkedHashSet<Director>> directors = filmStorage.getFilmsDirectors(films);
        fillGenresAndDirectorsForFilms(films, genres, directors);
        log.info("Обработан запрос на список рекомендованных фильмов для пользователя с ID {}", userId);
        return films;
    }

    /**
     * Метод заполнения жанров и режиссёров каждому фильму из списка со всеми фильмами
     *
     * @param films список фильмов, которым нужно найти жанры и режиссёров в системе,
     *              а также - заполнить соответствующие поля
     */
    private void fillGenresAndDirectorsForFilms(List<Film> films, Map<Long, LinkedHashSet<Genre>> genres,
                                                Map<Long, LinkedHashSet<Director>> directors) {
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(new LinkedHashSet<>(genres.get(film.getId())));
            }
            if (directors.containsKey(film.getId())) {
                film.setDirectors(new LinkedHashSet<>(directors.get(film.getId())));
            }
        }
    }
}

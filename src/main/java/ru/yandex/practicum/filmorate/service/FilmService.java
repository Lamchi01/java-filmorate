package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.util.LinkedHashSet;
import java.util.List;
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
    private final EventService eventService;
    private final FilmDbStorage filmDbStorage;

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
        film.setDirectors(new LinkedHashSet<>(directorStorage.getDirectors(film)));
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
            filmGenreStorage.deleteFilmGenres(film); // удалим все жанры фильмы из таблицы FILM_GENRES
            filmGenreStorage.addGenres(film, film.getGenres().stream().toList());
            // оставить для MockMvc тестов контроллеров, так как жанры могут приходить без имени
            film.setGenres(new LinkedHashSet<>(filmGenreStorage.getGenres(film)));
        }
        if (film.getDirectors() != null) {
            directorStorage.deleteFilmDirectors(film);
            directorStorage.addDirectors(film, film.getDirectors().stream().toList());
            film.setDirectors(new LinkedHashSet<>(directorStorage.getDirectors(film)));
        }
        if (film.getMpa() != null) savedFilm.setMpa(film.getMpa());
        if (film.getLikes() != null) savedFilm.setLikes(film.getLikes());

        filmStorage.update(film);
        return film;
    }

    public void deleteAllFilms() {
        filmStorage.deleteAll();
    }

    public Film likeFilm(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likeStorage.likeFilm(film, user);
        log.trace("Добавлен лайк к фильму с ID: {} пользователем с ID: {}", filmId, userId);
        eventService.addEvent(
                userId,
                LIKE,
                ADD,
                filmId
        );
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likeStorage.deleteLike(film, user);
        log.trace("Удален лайк к фильму с ID: {} пользователя с ID: {}", filmId, userId);
        eventService.addEvent(
                userId,
                LIKE,
                REMOVE,
                filmId
        );
        return film;
    }

    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        log.trace("Получен запрос на получение {} популярных фильмов с фильтрацией по жанру и году", count);
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public List<Film> findFilmsByDirectorId(long directorId, String sortedBy) {
        log.trace("Получен запрос на получение фильмов режиссёра с ID = {}", directorId);
        return filmStorage.findFilmsByDirectorId(directorId, sortedBy);
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteById(id);
        log.trace("Фильм с ID: {} успешно удалён", id);
    }

    public List<Film> findCommonFilms(long userId, long friendId) {
        return filmStorage.findCommonFilms(userId, friendId);
    }

    public List<Film> findFilms(String query, String by) {
        log.trace("Получен запрос на поиск фильмов. Строка поиска = {}", query);
        return filmStorage.findFilms(query, by);
    }

    public List<Film> getRecommendation(long userId) {
        log.trace("Получен запрос на список рекомендованных фильмов для пользователя с ID {}", userId);
        return filmDbStorage.getRecommendation(userId);
    }
}

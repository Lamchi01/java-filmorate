package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.Instant;
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
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.LIKE)
                .operation(Event.Operation.ADD)
                .entityId(filmId)
                .build();
        eventStorage.addEvent(event);
        log.info("Создано событие добавления like - {}", event);
        return film;
    }

    public Film deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        likeStorage.deleteLike(film, user);
        log.trace("Удален лайк к фильму с ID: {} пользователя с ID: {}", filmId, userId);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.LIKE)
                .operation(Event.Operation.REMOVE)
                .entityId(filmId)
                .build();
        eventStorage.addEvent(event);
        log.info("Создано событие удаления like - {}", event);
        return film;
    }

    public List<Film> popularFilms(int count) {
        log.trace("Получен запрос на получение {} популярных фильмов", count);
        return filmStorage.popularFilms(count);
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
}

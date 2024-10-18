package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM films";
    private static final String FIND_POPULAR_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        log.trace("Получен запрос на получение всех фильмов");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film findById(Long id) {
        log.trace("Получен запрос на получение фильма с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public Film create(Film film) {
        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        log.trace("Добавлен новый фильм с ID: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        log.trace("Обновлен фильм с ID: {}", film.getId());
        return film;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.trace("Удалены все фильмы");
    }

    @Override
    public List<Film> popularFilms(int count) {
        log.trace("Получен запрос на получение TOP {} популярных фильмов", count);
        return findMany(FIND_POPULAR_QUERY, count);
    }
}

package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id IN " +
            "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
    private static final String INSERT_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?) ";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    protected final JdbcTemplate jdbc;

    @Override
    public List<Genre> getGenres(Film film) {
        log.info("Получен запрос на получение жанров фильма с ID {}", film.getId());
        return jdbc.query(FIND_BY_ID_QUERY, new GenreRowMapper(), film.getId());
    }

    @Override
    public void addGenre(Film film, Genre genre) {
        jdbc.update(INSERT_QUERY, film.getId(), genre.getId());
        log.info("Добавлен жанр с ID {} к фильму с ID {}", genre.getId(), film.getId());
    }

    @Override
    public void addGenres(Film film, List<Genre> genres) {
        jdbc.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public void deleteFilmGenres(Film film) {
        jdbc.update(DELETE_BY_ID_QUERY, film.getId());
        log.info("УдалиЛи все жанры у фильма с ID {}", film.getId());
    }
}

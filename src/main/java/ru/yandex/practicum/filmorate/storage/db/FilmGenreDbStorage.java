package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id IN " +
            "(SELECT genre_id FROM film_genres WHERE film_id = ?)";
    private static final String FIND_ALL_FILM_GENRES = "SELECT fg.*, g.name genre_name FROM film_genres fg " +
            "LEFT JOIN genres g ON fg.genre_id = g.genre_id";
    private static final String INSERT_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?) ";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    protected final JdbcTemplate jdbc;

    @Override
    public List<Genre> getGenres(long filmId) {
        log.trace("Получен запрос на получение жанров фильма с ID {}", filmId);
        return jdbc.query(FIND_BY_ID_QUERY, new GenreRowMapper(), filmId);
    }

    @Override
    public void addGenre(long filmId, long genreId) {
        jdbc.update(INSERT_QUERY, filmId, genreId);
        log.trace("Добавлен жанр с ID {} к фильму с ID {}", genreId, filmId);
    }

    @Override
    public void addGenres(long filmId, List<Long> genresId) {
        jdbc.batchUpdate(INSERT_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genresId.get(i));
            }

            @Override
            public int getBatchSize() {
                return genresId.size();
            }
        });
    }

    @Override
    public void deleteFilmGenres(long filmId) {
        jdbc.update(DELETE_BY_ID_QUERY, filmId);
        log.trace("УдалиЛи все жанры у фильма с ID {}", filmId);
    }

    /**
     * Метод для выборки всех жанров всех фильмов
     *
     * @return - HashSet, ключ- ID фильма, значение - список жанров в виде объектов
     */
    @Override
    public Map<Long, LinkedHashSet<Genre>> getAllFilmGenres() {
        Map<Long, LinkedHashSet<Genre>> res = new HashMap<>();
        return jdbc.query(FIND_ALL_FILM_GENRES, (ResultSet rs) -> {
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long genreId = rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");
                res.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(new Genre(genreId, genreName));
            }
            return res;
        });
    }
}

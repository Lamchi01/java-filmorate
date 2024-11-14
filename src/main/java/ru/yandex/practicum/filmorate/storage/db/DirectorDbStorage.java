package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM directors WHERE director_id IN " +
            "(SELECT director_id FROM film_directors WHERE film_id = ?)";

    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String INSERT_FILM_DIRECTORS_RELATION_QUERY = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?) ";

    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";

    private static final String DELETE_ALL_QUERY = "DELETE FROM directors";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM directors WHERE director_id = ?";
    private static final String DELETE_FILM_DIRECTORS_RELATION_QUERY = "DELETE FROM film_directors WHERE film_id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);

    }

    @Override
    public Director findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));
    }

    @Override
    public Director create(Director director) {
        long id = insert(INSERT_QUERY, director.getName());
        log.info("Добавлен новый режиссёр с ID: {}", director.getId());
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        log.info("Обновлен режиссёр с ID: {}", director.getId());
        return director;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.info("Удалены все режиссёры");
    }

    @Override
    public List<Director> getDirectors(Film film) {
        log.info("Получен запрос на получение режиссёров фильма с ID {}", film.getId());
        return jdbc.query(FIND_BY_FILM_ID_QUERY, new DirectorRowMapper(), film.getId());
    }

    @Override
    public void addDirectors(Film film, List<Director> director) {
        jdbc.batchUpdate(INSERT_FILM_DIRECTORS_RELATION_QUERY, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, director.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return director.size();
            }
        });
    }

    @Override
    public void deleteFilmDirectors(Film film) {
        jdbc.update(DELETE_FILM_DIRECTORS_RELATION_QUERY, film.getId());
        log.info("Удалили всех режиссёров у фильма с ID {}", film.getId());
    }

    @Override
    public void deleteById(long id) {
        removeOne(DELETE_BY_ID_QUERY, id);
        log.info("Режиссёр с ID: {} успешно удалён", id);
    }

}

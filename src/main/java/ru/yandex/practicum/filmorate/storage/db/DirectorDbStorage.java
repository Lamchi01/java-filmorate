package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM directors";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM directors WHERE director_id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Director> findAll() {
        log.trace("Получен запрос на получение всех режиссёров");
        return findMany(FIND_ALL_QUERY);

    }

    @Override
    public Director findById(Long id) {
        log.trace("Получен запрос на получение режиссёра с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));
    }

    @Override
    public Director create(Director director) {
        long id = insert(INSERT_QUERY, director.getName());
        log.trace("Добавлен новый режиссёр с ID: {}", director.getId());
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        log.trace("Обновлен режиссёр с ID: {}", director.getId());
        return director;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.trace("Удалены все режиссёры");
    }

    @Override
    public void deleteById(long id) {
        removeOne(DELETE_BY_ID_QUERY, id);
        log.trace("Режиссёр с ID: {} успешно удалён", id);
    }

}

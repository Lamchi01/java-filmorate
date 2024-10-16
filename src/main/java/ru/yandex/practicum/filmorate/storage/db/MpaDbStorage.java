package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "false")
@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements BaseStorage<Mpa> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO mpa(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE mpa SET name = ? WHERE mpa_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM mpa";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findAll() {
        log.trace("Получен запрос на получение всех рейтингов MPA");
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa findById(Long id) {
        log.trace("Получен запрос на получение рейтинга MPA с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("MPA with id " + id + " not found"));
    }

    @Override
    public Mpa update(Mpa mpa) {
        update(UPDATE_QUERY, mpa.getName(), mpa.getId());
        log.trace("Обновлен рейтинг MPA с ID: {}", mpa.getId());
        return mpa;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.trace("Удалены все рейтинги MPA");
    }

    public Mpa create(Mpa mpa) {
        long id = insert(INSERT_QUERY, mpa.getName());
        mpa.setId(id);
        log.trace("Добавлен новый рейтинг MPA с ID: {}", mpa.getId());
        return mpa;
    }
}

package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
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
        log.info("Получен запрос на получение всех рейтингов MPA");
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa findById(Long id) {
        log.info("Получен запрос на получение рейтинга MPA с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("MPA with id " + id + " not found"));
    }

    @Override
    public Mpa update(Mpa mpa) {
        update(UPDATE_QUERY, mpa.getName(), mpa.getId());
        log.info("Обновлен рейтинг MPA с ID: {}", mpa.getId());
        return mpa;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.info("Удалены все рейтинги MPA");
    }

    @Override
    public void deleteById(long id) {

    }

    public Mpa create(Mpa mpa) {
        long id = insert(INSERT_QUERY, mpa.getName());
        mpa.setId(id);
        log.info("Добавлен новый рейтинг MPA с ID: {}", mpa.getId());
        return mpa;
    }
}

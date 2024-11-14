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
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM mpa WHERE mpa_id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findAll() {
        log.info("Получение всех рейтингов MPA");
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa findById(Long id) {
        log.info("Получение рейтинга MPA с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("MPA with id " + id + " not found"));
    }

    @Override
    public Mpa update(Mpa mpa) {
        log.info("Обновление рейтинга MPA с ID: {}", mpa.getId());
        update(UPDATE_QUERY, mpa.getName(), mpa.getId());
        return mpa;
    }

    @Override
    public void deleteAll() {
        log.info("Удаление всех рейтингов MPA");
        removeAll(DELETE_ALL_QUERY);
    }

    @Override
    public void deleteById(long id) {
        log.info("Удаление рейтинга с ID {}", id);
        removeOne(DELETE_BY_ID_QUERY, id);
    }

    public Mpa create(Mpa mpa) {
        log.info("Добавление нового рейтинга MPA");
        long id = insert(INSERT_QUERY, mpa.getName());
        mpa.setId(id);
        return mpa;
    }
}

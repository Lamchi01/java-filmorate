package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.sql.PreparedStatement;
import java.util.List;


@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected T findOne(String query, Object... params) {
        try {
            return jdbc.queryForObject(query, mapper, params);
        } catch (Exception e) {
            return null;
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected boolean delete(String query, Object... params) {
        int rowDeleted = jdbc.update(query, params);
        return rowDeleted > 0;
    }

    protected void update(String query, Object... args) {
        try {
            int rowsUpdated = jdbc.update(query, args);
            if (rowsUpdated == 0) {
                throw new NotFoundException("Не удалось обновить данные");
            }
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Не удалось обновить данные");
        }
    }

    protected Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con
                        .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Не удалось сохранить данные");
        }

        Integer id = keyHolder.getKeyAs(Integer.class);

        if (id != null) {
            return id;
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    protected void batchUpdateBase(String query, BatchPreparedStatementSetter bps) {
        try {
            int[] rowsUpdated = jdbc.batchUpdate(query, bps);
            if (rowsUpdated.length == 0) {
                throw new NotFoundException("Не удалось обновить данные");
            }
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Не удалось обновить данные");
        }
    }
}
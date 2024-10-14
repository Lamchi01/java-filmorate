package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        return List.of();
    }

    @Override
    public void create(Film film) {

    }

    @Override
    public Film findById(Long id) {
        return null;
    }

    @Override
    public void update(Film film) {

    }

    @Override
    public void deleteAll() {

    }
}

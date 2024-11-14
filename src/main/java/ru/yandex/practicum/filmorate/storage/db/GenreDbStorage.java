package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@Component
public class GenreDbStorage extends BaseDbStorage<Genre> implements BaseStorage<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO genres (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE genres SET name = ? WHERE genre_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM genres";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM genres WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        log.info("Получение всех жанров");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Genre create(Genre genre) {
        log.info("Добавление нового жанра с ID: {}", genre.getId());
        long id = insert(INSERT_QUERY, genre.getName());
        genre.setId(id);
        return genre;
    }

    @Override
    public Genre findById(Long id) {
        log.info("Получение жанра с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Genre with id " + id + " not found"));
    }

    @Override
    public Genre update(Genre genre) {
        log.info("Обновление жанра с ID: {}", genre.getId());
        update(UPDATE_QUERY, genre.getName(), genre.getId());
        return genre;
    }

    @Override
    public void deleteAll() {
        log.info("Удаление всех жанров");
        removeAll(DELETE_ALL_QUERY);
    }

    @Override
    public void deleteById(long id) {
        log.info("Удаление жанра с ID {}", id);
        removeOne(DELETE_BY_ID_QUERY, id);
    }
}

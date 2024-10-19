package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    public void findAll() {
        assertEquals(6, genreStorage.findAll().size());
    }

    @Test
    public void deleteAll() {
        genreStorage.deleteAll();
        assertTrue(genreStorage.findAll().isEmpty());
    }

    @Test
    public void findById() {
        Genre genre = genreStorage.findById(1L);
        assertEquals(1L, genre.getId());
        assertFalse(genre.getName().isEmpty());

        // поиск несуществующего ID
        assertThrows(NotFoundException.class, () -> genreStorage.findById(Long.MAX_VALUE));
    }

    @Test
    public void create() {
        Genre genre = new Genre(null, "New genre");
        long id = genreStorage.create(genre).getId();
        genre = genreStorage.findById(id);
        assertEquals(genre, genreStorage.findById(id));
    }

    @Test
    public void update() {
        Genre genre = genreStorage.findAll().getFirst();
        genre.setName("Updated");
        genreStorage.update(genre);
        assertEquals("Updated", genreStorage.findById(genre.getId()).getName());

        // обновление не существующего объекта
        genre.setId(Long.MAX_VALUE);
        assertThrows(InternalServerException.class, () -> genreStorage.update(genre));
    }
}

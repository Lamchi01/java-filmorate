package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"app.storage.in-memory=false"})
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    public void findAll() {
        assertEquals(6, genreDbStorage.findAll().size());
    }

    @Test
    public void deleteAll() {
        genreDbStorage.deleteAll();
        assertTrue(genreDbStorage.findAll().isEmpty());
    }

    @Test
    public void findById() {
        Genre genre = genreDbStorage.findById(1L);
        assertEquals(1L, genre.getId());
        assertFalse(genre.getName().isEmpty());

        // поиск несуществующего ID
        assertThrows(NotFoundException.class, () -> genreDbStorage.findById(Long.MAX_VALUE));
    }

    @Test
    public void create() {
        Genre genre = new Genre(null, "New genre");
        long id = genreDbStorage.create(genre).getId();
        genre = genreDbStorage.findById(id);
        assertEquals(genre, genreDbStorage.findById(id));
    }

    @Test
    public void update() {
        Genre genre = genreDbStorage.findAll().getFirst();
        genre.setName("Updated");
        genreDbStorage.update(genre);
        assertEquals("Updated", genreDbStorage.findById(genre.getId()).getName());

        // обновление не существующего объекта
        genre.setId(Long.MAX_VALUE);
        assertThrows(InternalServerException.class, () -> genreDbStorage.update(genre));
    }
}

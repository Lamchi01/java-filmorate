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
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.MpaRowMapper;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"app.storage.in-memory=false"})
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void findAll() {
        assertEquals(5, mpaDbStorage.findAll().size());
    }

    @Test
    public void deleteAll() {
        mpaDbStorage.deleteAll();
        assertTrue(mpaDbStorage.findAll().isEmpty());
    }

    @Test
    public void findById() {
        Mpa mpa = mpaDbStorage.findById(1L);
        assertEquals(1L, mpa.getId());
        assertFalse(mpa.getName().isEmpty());

        // поиск несуществующего ID
        assertThrows(NotFoundException.class, () -> mpaDbStorage.findById(Long.MAX_VALUE));
    }

    @Test
    public void create() {
        Mpa mpa = new Mpa(null, "New MPA");
        long id = mpaDbStorage.create(mpa).getId();
        mpa = mpaDbStorage.findById(id);
        assertEquals(mpa, mpaDbStorage.findById(id));
    }

    @Test
    public void update() {
        Mpa mpa = mpaDbStorage.findAll().getFirst();
        mpa.setName("Updated");
        mpaDbStorage.update(mpa);
        assertEquals("Updated", mpaDbStorage.findById(mpa.getId()).getName());

        // обновление не существующего объекта
        mpa.setId(Long.MAX_VALUE);
        assertThrows(InternalServerException.class, () -> mpaDbStorage.update(mpa));
    }
}

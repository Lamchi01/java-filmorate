package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.MpaRowMapper;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    public void findAll() {
        assertEquals(5, mpaStorage.findAll().size());
    }

    @Test
    public void deleteAll() {
        mpaStorage.deleteAll();
        assertTrue(mpaStorage.findAll().isEmpty());
    }

    @Test
    public void findById() {
        Mpa mpa = mpaStorage.findById(1L);
        assertEquals(1L, mpa.getId());
        assertFalse(mpa.getName().isEmpty());

        // поиск несуществующего ID
        assertThrows(NotFoundException.class, () -> mpaStorage.findById(Long.MAX_VALUE));
    }

    @Test
    public void create() {
        Mpa mpa = new Mpa(null, "New MPA");
        long id = mpaStorage.create(mpa).getId();
        mpa = mpaStorage.findById(id);
        assertEquals(mpa, mpaStorage.findById(id));
    }

    @Test
    public void update() {
        Mpa mpa = mpaStorage.findAll().getFirst();
        mpa.setName("Updated");
        mpaStorage.update(mpa);
        assertEquals("Updated", mpaStorage.findById(mpa.getId()).getName());

        // обновление не существующего объекта
        mpa.setId(Long.MAX_VALUE);
        assertThrows(InternalServerException.class, () -> mpaStorage.update(mpa));
    }
}

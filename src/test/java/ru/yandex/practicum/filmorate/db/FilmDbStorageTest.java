package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"app.storage.in-memory=false"})
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
public class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    Film film1;
    Film film2;
    Film film3;

    @BeforeEach
    public void beforeEach() {
        film1 = new Film(null, "Film1", "desc1", LocalDate.of(1991, 1, 1), 110L, new Mpa(1L, ""), null, null, 0);
        film1 = filmStorage.create(film1);
        film2 = new Film(null, "Film2", "desc2", LocalDate.of(1992, 1, 1), 110L, new Mpa(2L, ""), null, null, 0);
        film2 = filmStorage.create(film2);
        film3 = new Film(null, "Film3", "desc3", LocalDate.of(1993, 1, 1), 110L, new Mpa(3L, ""), null, null, 0);
        film3 = filmStorage.create(film3);
    }

    @Test
    public void findAll() {
        assertEquals(3, filmStorage.findAll().size());
        assertEquals(List.of(film1, film2, film3), filmStorage.findAll());
    }

    @Test
    public void findById() {
        assertEquals(film2, filmStorage.findById(film2.getId()));

        // поиск несуществующего ID
        assertThrows(NotFoundException.class, () -> filmStorage.findById(Long.MAX_VALUE));
    }

    @Test
    public void create() {
        Film film = new Film(null, "Film4", "desc4", LocalDate.of(1994, 1, 1), 110L, new Mpa(3L, ""), null, null, 0);
        long id = filmStorage.create(film).getId();
        Film newFilm = filmStorage.findById(id);
        assertEquals(film, newFilm);
        assertEquals(4L, filmStorage.findAll().size());
    }

    @Test
    public void update() {
        Film film = filmStorage.findById(film1.getId());
        film.setMpa(new Mpa(1L, ""));
        filmStorage.update(film);
        Film updatedFilm = filmStorage.findById(film.getId());
        assertEquals(film, updatedFilm);

        // обновление не существующего объекта
        film.setId(Long.MAX_VALUE);
        assertThrows(InternalServerException.class, () -> filmStorage.update(film));
    }

    @Test
    public void deleteAll() {
        filmStorage.deleteAll();
        assertTrue(filmStorage.findAll().isEmpty());
    }

    @Test
    public void popularFilms() {
        assertEquals(3, filmStorage.popularFilms(100).size());
        assertEquals(2, filmStorage.popularFilms(2).size());
        assertEquals(0, filmStorage.popularFilms(0).size());
    }
}

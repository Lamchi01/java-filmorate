package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"app.storage.in-memory=false"})
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmGenreDbStorage.class, FilmDbStorage.class, FilmRowMapper.class})
public class FilmGenreDbStorageTest {
    private final FilmGenreDbStorage filmGenreStorage;
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

        filmGenreStorage.addGenre(film1.getId(), 1L);
        filmGenreStorage.addGenre(film1.getId(), 2L);
        filmGenreStorage.addGenre(film1.getId(), 3L);
        filmGenreStorage.addGenre(film2.getId(), 4L);
        filmGenreStorage.addGenre(film3.getId(), 4L);
        filmGenreStorage.addGenre(film3.getId(), 5L);
    }

    @Test
    public void getGenres() {
        assertEquals(3L, filmGenreStorage.getGenres(film1.getId()).size());
        assertEquals(1L, filmGenreStorage.getGenres(film2.getId()).size());
        assertEquals(2L, filmGenreStorage.getGenres(film3.getId()).size());
    }

    @Test
    public void addGenre() {
        filmGenreStorage.addGenre(film2.getId(), 5L);
        List<Long> genresId = filmGenreStorage.getGenres(film2.getId()).stream().map(Genre::getId).toList();
        assertEquals(List.of(4L, 5L), genresId);

        // проверка на дубликат жанра, должно быть исключение
        assertThrows(DuplicateKeyException.class, () -> filmGenreStorage.addGenre(film2.getId(), 5L));
    }

    @Test
    public void deleteFilmGenres() {
        filmGenreStorage.deleteFilmGenres(film1.getId());
        assertTrue(filmGenreStorage.getGenres(film1.getId()).isEmpty());
        assertEquals(1L, filmGenreStorage.getGenres(film2.getId()).size());
        assertEquals(2L, filmGenreStorage.getGenres(film3.getId()).size());
    }
}

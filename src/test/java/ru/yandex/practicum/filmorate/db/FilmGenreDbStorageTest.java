package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmGenreDbStorage.class, FilmDbStorage.class, FilmRowMapper.class, GenreDbStorage.class, GenreRowMapper.class})
public class FilmGenreDbStorageTest {
    private final FilmGenreDbStorage filmGenreStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;

    Film film1;
    Film film2;
    Film film3;

    @BeforeEach
    public void beforeEach() {
        film1 = new Film(null, "Film1", "desc1", LocalDate.of(1991, 1, 1), 110L, new Mpa(1L, ""), null, null, null, 0L);
        film1 = filmStorage.create(film1);
        film2 = new Film(null, "Film2", "desc2", LocalDate.of(1992, 1, 1), 110L, new Mpa(2L, ""), null, null, null, 0L);
        film2 = filmStorage.create(film2);
        film3 = new Film(null, "Film3", "desc3", LocalDate.of(1993, 1, 1), 110L, new Mpa(3L, ""), null, null, null, 0L);
        film3 = filmStorage.create(film3);

        filmGenreStorage.addGenre(film1, genreStorage.findById(1L));
        filmGenreStorage.addGenre(film1, genreStorage.findById(2L));
        filmGenreStorage.addGenre(film1, genreStorage.findById(3L));
        filmGenreStorage.addGenre(film2, genreStorage.findById(4L));
        filmGenreStorage.addGenre(film3, genreStorage.findById(4L));
        filmGenreStorage.addGenre(film3, genreStorage.findById(5L));
    }

    @Test
    public void getGenres() {
        assertEquals(3L, filmGenreStorage.getGenres(film1).size());
        assertEquals(1L, filmGenreStorage.getGenres(film2).size());
        assertEquals(2L, filmGenreStorage.getGenres(film3).size());
    }

    @Test
    public void addGenre() {
        filmGenreStorage.addGenre(film2, genreStorage.findById(5L));
        List<Genre> genres = filmGenreStorage.getGenres(film2).stream().toList();
        assertEquals(List.of(genreStorage.findById(4L), genreStorage.findById(5L)), genres);

        // проверка на дубликат жанра, должно быть исключение
        assertThrows(DuplicateKeyException.class, () -> filmGenreStorage.addGenre(film2, genreStorage.findById(5L)));
    }

    @Test
    public void deleteFilmGenres() {
        filmGenreStorage.deleteFilmGenres(film1);
        assertTrue(filmGenreStorage.getGenres(film1).isEmpty());
        assertEquals(1L, filmGenreStorage.getGenres(film2).size());
        assertEquals(2L, filmGenreStorage.getGenres(film3).size());
    }
}

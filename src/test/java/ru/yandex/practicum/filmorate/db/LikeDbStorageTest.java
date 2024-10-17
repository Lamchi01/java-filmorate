package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestPropertySource(properties = {"app.storage.in-memory=false"})
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeDbStorage.class, FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class})
public class LikeDbStorageTest {
    private final LikeDbStorage likeStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    Film film1;
    Film film2;
    Film film3;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "email1@mail.ru", "login1", "user1", LocalDate.of(1980, 1, 1), null);
        user1 = userStorage.create(user1);
        user2 = new User(null, "email2@mail.ru", "login2", "user2", LocalDate.of(1980, 1, 2), null);
        user2 = userStorage.create(user2);
        user3 = new User(null, "email3@mail.ru", "login3", "user3", LocalDate.of(1980, 1, 3), null);
        user3 = userStorage.create(user3);

        film1 = new Film(null, "Film1", "desc1", LocalDate.of(1991, 1, 1), 110L, new Mpa(1L, ""), null, null, 0);
        film1 = filmStorage.create(film1);
        film2 = new Film(null, "Film2", "desc2", LocalDate.of(1992, 1, 1), 110L, new Mpa(2L, ""), null, null, 0);
        film2 = filmStorage.create(film2);
        film3 = new Film(null, "Film3", "desc3", LocalDate.of(1993, 1, 1), 110L, new Mpa(3L, ""), null, null, 0);
        film3 = filmStorage.create(film3);
    }

    @Test
    public void likeFilm() {
        likeStorage.likeFilm(film3.getId(), user1.getId());
        likeStorage.likeFilm(film3.getId(), user2.getId());
        likeStorage.likeFilm(film3.getId(), user3.getId());
        likeStorage.likeFilm(film2.getId(), user1.getId());
        likeStorage.likeFilm(film2.getId(), user2.getId());
        likeStorage.likeFilm(film1.getId(), user1.getId());

        assertEquals(List.of(film3, film2, film1), filmStorage.popularFilms(3));

        // проверка на дубликат лайка, должно быть исключение
        assertThrows(DuplicateKeyException.class, () -> likeStorage.likeFilm(film3.getId(), user1.getId()));
    }

    @Test
    public void deleteLike() {
        likeStorage.likeFilm(film1.getId(), user1.getId());

        // проверка на дубликат лайка, должно быть исключение
        assertThrows(DuplicateKeyException.class, () -> likeStorage.likeFilm(film1.getId(), user1.getId()));

        likeStorage.likeFilm(film1.getId(), user2.getId());
        likeStorage.likeFilm(film2.getId(), user1.getId());
        assertEquals(List.of(film1, film2), filmStorage.popularFilms(2));

        likeStorage.deleteLike(film2.getId(), user1.getId());
        assertEquals(List.of(film1), filmStorage.popularFilms(1));

        // удаляем остальные два лайка
        likeStorage.deleteLike(film1.getId(), user1.getId());
        likeStorage.deleteLike(film1.getId(), user2.getId());

        // удаляем лайк не существующего фильма, должно быть исключение (вся проверка в сервисном слое)
        assertThrows(DataIntegrityViolationException.class, () -> likeStorage.likeFilm(Long.MAX_VALUE, user1.getId()));

    }
}

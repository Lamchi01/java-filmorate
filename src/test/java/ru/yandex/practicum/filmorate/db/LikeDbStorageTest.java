package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        user1 = new User(null, "email1@mail.ru", "login1", "user1", LocalDate.of(1980, 1, 1), new HashSet<>());
        user1 = userStorage.create(user1);
        user2 = new User(null, "email2@mail.ru", "login2", "user2", LocalDate.of(1980, 1, 2), new HashSet<>());
        user2 = userStorage.create(user2);
        user3 = new User(null, "email3@mail.ru", "login3", "user3", LocalDate.of(1980, 1, 3), new HashSet<>());
        user3 = userStorage.create(user3);

        film1 = new Film(null, "Film1", "desc1", LocalDate.of(1991, 1, 1), 110L, new Mpa(1L, "G"), new LinkedHashSet<>(),
                new LinkedHashSet<>(), new HashSet<>(), 0L);
        film1 = filmStorage.create(film1);
        film2 = new Film(null, "Film2", "desc2", LocalDate.of(1992, 1, 1), 110L, new Mpa(2L, "PG"), new LinkedHashSet<>(),
                new LinkedHashSet<>(), new HashSet<>(), 0L);
        film2 = filmStorage.create(film2);
        film3 = new Film(null, "Film3", "desc3", LocalDate.of(1993, 1, 1), 110L, new Mpa(3L, "PG-13"),
                new LinkedHashSet<>(), new LinkedHashSet<>(), new HashSet<>(), 0L);
        film3 = filmStorage.create(film3);
    }

    @Test
    public void likeFilm() {
        likeStorage.likeFilm(film3, user1);
        likeStorage.likeFilm(film3, user2);
        likeStorage.likeFilm(film3, user3);
        likeStorage.updateCountLikes(film3);
        likeStorage.likeFilm(film2, user1);
        likeStorage.likeFilm(film2, user2);
        likeStorage.updateCountLikes(film2);
        likeStorage.likeFilm(film1, user1);
        likeStorage.updateCountLikes(film1);

        assertEquals(List.of(film3, film2, film1), filmStorage.getPopularFilms(3, null, null));

        // количество лайков должно совпадать с выборкой из БД
        assertEquals(film3.getCountLikes(), likeStorage.getLikes(film3));
        assertEquals(film2.getCountLikes(), likeStorage.getLikes(film2));
        assertEquals(film1.getCountLikes(), likeStorage.getLikes(film1));

        // проверка на дубликат лайка, должно быть исключение
        assertDoesNotThrow(() -> likeStorage.likeFilm(film3, user1));

        // добавить лайк не существующего фильма, должно быть исключение
        Film film = new Film(1000L, "Film1000", "desc1000", LocalDate.of(1993, 1, 1), 110L, new Mpa(3L, "PG-13"),
                new LinkedHashSet<>(), new LinkedHashSet<>(), new HashSet<>(), 0L);
        assertThrows(DataIntegrityViolationException.class, () -> likeStorage.likeFilm(film, user1));

        // добавить лайк от не существующего юзера, должно быть исключение
        User user = new User(1000L, "email3@mail.ru", "login3", "user3", LocalDate.of(1980, 1, 3), new HashSet<>());
        assertThrows(DataIntegrityViolationException.class, () -> likeStorage.likeFilm(film1, user));
    }

    @Test
    public void deleteLike() {
        // удалить лайк не существующего фильма, должно быть исключение
        Film film = new Film(1000L, "Film1000", "desc1000", LocalDate.of(1993, 1, 1), 110L, new Mpa(3L, "PG-13"),
                new LinkedHashSet<>(), new LinkedHashSet<>(), new HashSet<>(), 0L);
        assertDoesNotThrow(() -> likeStorage.deleteLike(film, user1));

        // удалить лайк от не существующего юзера, должно быть исключение
        User user = new User(1000L, "email3@mail.ru", "login3", "user3", LocalDate.of(1980, 1, 3), new HashSet<>());
        assertDoesNotThrow(() -> likeStorage.deleteLike(film1, user));

        likeStorage.likeFilm(film1, user1);

        // проверка на дубликат лайка, должно быть исключение
        assertDoesNotThrow(() -> likeStorage.likeFilm(film1, user1));

        likeStorage.likeFilm(film1, user2);
        likeStorage.likeFilm(film2, user1);
        assertEquals(List.of(film1, film2), filmStorage.getPopularFilms(2, null, null));

        likeStorage.deleteLike(film2, user1);
        assertEquals(List.of(film1), filmStorage.getPopularFilms(1, null, null));

        // удаляем остальные два лайка
        likeStorage.deleteLike(film1, user1);
        likeStorage.deleteLike(film1, user2);
    }

    @Test
    public void getLikes() {
        likeStorage.likeFilm(film3, user1);
        likeStorage.likeFilm(film3, user2);
        likeStorage.likeFilm(film3, user3);
        likeStorage.updateCountLikes(film3);
        likeStorage.likeFilm(film2, user1);
        likeStorage.likeFilm(film2, user2);
        likeStorage.updateCountLikes(film2);
        likeStorage.likeFilm(film1, user1);
        likeStorage.updateCountLikes(film1);

        // количество лайков должно совпадать с выборкой из БД
        assertEquals(film3.getCountLikes(), likeStorage.getLikes(film3));
        assertEquals(film2.getCountLikes(), likeStorage.getLikes(film2));
        assertEquals(film1.getCountLikes(), likeStorage.getLikes(film1));

        likeStorage.deleteLike(film3, user1);
        likeStorage.deleteLike(film3, user2);
        likeStorage.updateCountLikes(film3);
        assertEquals(1L, film3.getCountLikes());
        assertEquals(film3.getCountLikes(), likeStorage.getLikes(film3));
    }
}

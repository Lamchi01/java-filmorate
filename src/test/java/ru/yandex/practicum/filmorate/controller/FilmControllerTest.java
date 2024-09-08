package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void createFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(java.time.LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        filmController.create(film);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void errorCreateFilmDuration() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(java.time.LocalDate.of(2022, 1, 1))
                .duration(-100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void errorCreateFilmName() {
        Film film = Film.builder()
                .name(null)
                .description("Test description")
                .releaseDate(java.time.LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void errorCreateFilmDescription() {
        Film film = Film.builder()
                .name("Test Film")
                .description(null)
                .releaseDate(java.time.LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void errorCreateFilmReleaseDateBeforeStartFilmsDate() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }
}
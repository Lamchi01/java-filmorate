package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;
    private FilmController filmController;

    @BeforeEach
    void beforeEach() {
        FilmStorage filmStorageFiles = new InMemoryFilmStorage();
        filmController = new FilmController(new FilmService(filmStorageFiles));
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void afterEach() {
        validatorFactory.close();
    }

    @Test
    void createFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        filmController.create(film);

        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void errorCreateEmptyFilm() {
        Film film = Film.builder().build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void errorCreateNullNameFilm() {
        Film film = Film.builder()
                .name(null)
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    void errorCreateBlankNameFilm() {
        Film film = Film.builder()
                .name(" ")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    void errorCreateEmptyNameFilm() {
        Film film = Film.builder()
                .name("")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("name"))
                .count());
    }

    @Test
    void errorCreateNullDescriptionFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description(null)
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateBlankDescriptionFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description(" ")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateEmptyDescriptionFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateDescriptionTooLongFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("O".repeat(300))
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("description"))
                .count());
    }

    @Test
    void errorCreateNullReleaseDateFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(null)
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("releaseDate"))
                .count());
    }

    @Test
    void errorCreateReleaseDateBeforeFilmsDateFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("releaseDate"))
                .count());
    }

    @Test
    void errorCreateNegativeDurationFilm() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(-100)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("duration"))
                .count());
    }

    @Test
    void errorUpdateFilmEmptyId() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        filmController.create(film);

        Film newfilm = Film.builder()
                .id(null)
                .duration(150)
                .build();

        Exception exception = assertThrows(NotFoundException.class, () -> filmController.update(newfilm));
        assertEquals("Фильм с указанным id не найден", exception.getMessage());
    }

    @Test
    void errorUpdateFilmNotFoundId() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        filmController.create(film);

        Film newfilm = Film.builder()
                .id(5)
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Exception exception = assertThrows(NotFoundException.class, () -> filmController.update(newfilm));
        assertEquals("Фильм с указанным id не найден", exception.getMessage());
    }

    @Test
    void errorUpdateFilmLongDescription() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        filmController.create(film);

        Film newfilm = Film.builder()
                .id(1)
                .name("Test Film")
                .description("O".repeat(300))
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> filmController.update(newfilm));
        assertEquals("Длина описания не может превышать 200 символов", exception.getMessage());
    }

    @Test
    void errorUpdateFilmReleasedDateBeforeBirthdayFilms() {
        Film film = Film.builder()
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(100)
                .build();

        filmController.create(film);

        Film newfilm = Film.builder()
                .id(1)
                .name("Test Film")
                .description("Test description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> filmController.update(newfilm));
        assertEquals("Фильм не может быть раньше даты рождения фильмов", exception.getMessage());
    }
}
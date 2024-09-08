package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmTest {
    ValidatorFactory factory;
    Validator validator;
    Film film;
    Set<ConstraintViolation<Film>> violations;

    @BeforeEach
    public void beforeEach() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        film = new Film();
        film.setName("Film1");
        film.setDescription("Description1");
        film.setReleaseDate(LocalDate.of(2020, 2, 2));
        film.setDuration(1100L);
    }

    @AfterEach
    public void afterEach() {
        factory.close();
    }

    @Test
    public void validFilm() {
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongEmail() {
        film.setName("");
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("name")));

        film.setName(null);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void wrongDescription() {
        film.setDescription("s".repeat(201));
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("description")));

        film.setDescription(null);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("description")));
    }

    @Test
    public void wrongReleaseDate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    public void wrongDuration() {
        film.setDuration(-1L);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("duration")));

        film.setDuration(null);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("duration")));
    }
}

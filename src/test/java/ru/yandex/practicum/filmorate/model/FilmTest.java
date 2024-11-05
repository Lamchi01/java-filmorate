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

import static org.junit.jupiter.api.Assertions.*;

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
        film.setMpa(new Mpa(1L, "G"));
    }

    @AfterEach
    public void afterEach() {
        factory.close();
    }

    @Test
    public void validFilmOnCreate() {
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validFilmOnUpdate() {
        film.setId(1L);
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void nullIdOnCreate() {
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void notNullIdOnUpdate() {
        film.setId(1L);
        violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongNameOnCreate() {
        film.setName("");
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void nullNameOnCreate() {
        film.setName(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void wrongNameOnUpdate() {
        film.setName("");
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void nullNameOnUpdate() {
        film.setName(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void wrongDescriptionOnCreateOrOnUpdate() {
        film.setDescription("s".repeat(201));
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("description")));
    }

    @Test
    public void nullDescriptionOnCreate() {
        film.setDescription(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("description")));
    }

    @Test
    public void nullDescriptionOnUpdate() {
        film.setDescription(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("description")));
    }

    @Test
    public void wrongReleaseDateOnCreateOrOnUpdate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    public void nullReleaseDateOnCreate() {
        film.setReleaseDate(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    public void nullReleaseDateOnUpdate() {
        film.setReleaseDate(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    public void wrongDurationOnCreateOrOnUpdate() {
        film.setDuration(-1L);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void nullDurationOnCreate() {
        film.setDuration(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void nullDurationOnUpdate() {
        film.setDuration(null);
        violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void notNullIdAndAllFieldsIsNullWhenUpdate() {
        film.setId(1L);
        film.setName(null);
        film.setDescription(null);
        film.setDuration(null);
        film.setReleaseDate(null);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}

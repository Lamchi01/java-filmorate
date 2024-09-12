package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.validator.Marker;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    ValidatorFactory factory;
    Validator validator;
    User user;
    Set<ConstraintViolation<User>> violations;

    @BeforeEach
    public void beforeEach() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        user = new User();
        user.setName("User1");
        user.setLogin("login1");
        user.setEmail("user1@email.ru");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @AfterEach
    public void afterEach() {
        factory.close();
    }

    @Test
    public void validUserOnCreate() {
        violations = validator.validate(user, Marker.OnCreate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validUserOnUpdate() {
        user.setId(1L);
        violations = validator.validate(user, Marker.OnUpdate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void nullIdOnCreate() {
        violations = validator.validate(user, Marker.OnCreate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void nullIdOnUpdate() {
        violations = validator.validate(user, Marker.OnUpdate.class);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("id")));
    }

    @Test
    public void notNullIdOnCreate() {
        user.setId(1L);
        violations = validator.validate(user, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("id")));
    }

    @Test
    public void notNullIdOnUpdate() {
        user.setId(1L);
        violations = validator.validate(user, Marker.OnUpdate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongFormatEmailOnCreateOrOnUpdate() {
        user.setEmail("email");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));

        user.setEmail("email@email@email");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));

        user.setEmail("email email");
        violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void blankEmailOnCreate() {
        user.setEmail("");
        violations = validator.validate(user, Marker.OnCreate.class);
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void blankEmailOnUpdate() {
        user.setEmail("");
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void nullEmailOnCreate() {
        user.setEmail(null);
        violations = validator.validate(user, Marker.OnCreate.class);
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void nullEmailOnUpdate() {
        user.setEmail(null);
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongLoginOnCreateOrUpdate() {
        user.setLogin("");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("login")));

        user.setLogin(" ");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("login")));

        user.setLogin("login login");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("login")));
    }

    @Test
    public void nullLoginOnCreate() {
        user.setLogin(null);
        violations = validator.validate(user, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("login")));
    }

    @Test
    public void nullLoginOnUpdate() {
        user.setLogin(null);
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongBirthdayOnCreateOrOnUpdate() {
        user.setBirthday(LocalDate.of(2030, 1, 1));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    public void nullBirthdayOnCreate() {
        user.setBirthday(null);
        violations = validator.validate(user, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("birthday")));
    }

    @Test
    public void nullBirthdayOnUpdate() {
        user.setBirthday(null);
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void notNullIdAndAllFieldsIsNullWhenUpdate() {
        user.setId(1L);
        user.setName(null);
        user.setLogin(null);
        user.setEmail(null);
        user.setBirthday(null);
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }


}

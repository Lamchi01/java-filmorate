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
        user.setLogin("user1");
        user.setEmail("user1@email.ru");
        user.setId(1L);
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @AfterEach
    public void afterEach() {
        factory.close();
    }

    @Test
    public void validUser() {
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongEmail() {
        user.setEmail("emailemail");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));

        user.setEmail("");
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));

        user.setEmail(null);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void wrongLogin() {
        user.setLogin("login login");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("login")));

        user.setLogin("");
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("login")));

        user.setLogin(null);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("login")));
    }

    @Test
    public void wrongBirthday() {
        user.setBirthday(LocalDate.of(2030, 1, 1));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(o -> o.getPropertyPath().toString().equals("birthday")));
    }
}

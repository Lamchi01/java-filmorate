package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;
    private UserController userController;

    @BeforeAll
    static void beforeAll() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void afterAll() {
        validatorFactory.close();
    }

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUser() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .login("testUser")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.create(user);
        assertEquals(1, userController.getUsers().size());
    }

    @Test
    void errorCreateEmptyUser() {
        User user = User.builder().build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    void errorCreateInvalidEmail() {
        User user = User.builder()
                .email("testemail.com@")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("email"))
                .count());
    }

    @Test
    void errorCreateNullEmail() {
        User user = User.builder()
                .email(null)
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("email"))
                .count());
    }

    @Test
    void errorCreateNullLogin() {
        User user = User.builder()
                .email("test@example.com")
                .login(null)
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    void errorCreateBlankLogin() {
        User user = User.builder()
                .email("test@example.com")
                .login(" ")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    void errorCreateEmptyLogin() {
        User user = User.builder()
                .email("test@example.com")
                .login("")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    void errorSpaceInLogin() {
        User user = User.builder()
                .email("test@example.com")
                .login("test User")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .count());
    }

    @Test
    void errorCreateInvalidBirthday() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.now().plusYears(1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("birthday"))
                .count());
    }

    @Test
    void errorCreateInvalidNullBirthday() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(null)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("birthday"))
                .count());
    }

    @Test
    void errorUpdateUserEmptyId() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.create(user);

        User newUser = User.builder()
                .id(null)
                .name("Test User 2.0")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> userController.update(newUser));
        assertEquals("Id пользователя должно быть указано", exception.getMessage());
    }

    @Test
    void errorUpdateUserNotFoundId() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.create(user);

        User newUser = User.builder()
                .id(5)
                .name("Test User 2.0")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> userController.update(newUser));
        assertEquals("Пользователь с таким id не найден", exception.getMessage());
    }

    @Test
    void errorUpdateUserInvalidEmail() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.create(user);

        User newUser = User.builder()
                .id(1)
                .email("testexample.com@")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> userController.update(newUser));
        assertEquals("Email не соответствет стандарту", exception.getMessage());
    }

    @Test
    void errorUpdateUserBlankLogin() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.create(user);

        User newUser = User.builder()
                .id(1)
                .login(" ")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> userController.update(newUser));
        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void errorUpdateUserInvalidBirthday() {
        User user = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.create(user);

        User newUser = User.builder()
                .id(1)
                .birthday(LocalDate.now().plusYears(1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> userController.update(newUser));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
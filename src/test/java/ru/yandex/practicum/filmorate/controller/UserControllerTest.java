package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUser() {
      User user = User.builder()
              .email("test@email.com")
              .login("testUser")
              .name("Test User")
              .birthday(LocalDate.of(1990, 1, 1))
              .build();

      userController.create(user);

      assertEquals(1, userController.getUsers().size());
    }

    @Test
    void errorEmail() {
        User user = User.builder()
               .email("testemail.com")
               .login("testUser")
               .name("Test User")
               .birthday(LocalDate.of(1990, 1, 1))
               .build();

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void errorLogin() {
        User user = User.builder()
               .email("test@email.com")
               .login(null)
               .name("Test User")
               .birthday(LocalDate.of(1990, 1, 1))
               .build();

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void errorName() {
        User user = User.builder()
               .email("test@email.com")
               .login("testUser")
               .name("")
               .birthday(LocalDate.of(1990, 1, 1))
               .build();

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    void errorBirthday() {
        User user = User.builder()
               .email("test@email.com")
               .login("testUser")
               .name("Test User")
               .birthday(LocalDate.now().plusYears(1))
               .build();

        assertThrows(ValidationException.class, () -> userController.create(user));
    }
}
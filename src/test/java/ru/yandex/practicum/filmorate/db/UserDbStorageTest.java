package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageTest {
    private final UserDbStorage userStorage;
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
    }

    @Test
    public void findAll() {
        assertEquals(3, userStorage.findAll().size());
        assertEquals(List.of(user1, user2, user3), userStorage.findAll());
    }

    @Test
    public void findById() {
        assertEquals(user2, userStorage.findById(user2.getId()));

        // поиск несуществующего ID
        assertThrows(NotFoundException.class, () -> userStorage.findById(Long.MAX_VALUE));
    }

    @Test
    public void create() {
        User user = new User(null, "email4@mail.ru", "login4", "user4", LocalDate.of(1980, 1, 4), new HashSet<>());
        long id = userStorage.create(user).getId();
        User newUser = userStorage.findById(id);
        assertEquals(user, newUser);
        assertEquals(4L, userStorage.findAll().size());
    }

    @Test
    public void update() {
        User user = userStorage.findById(user1.getId());
        user.setName("Name updated");
        userStorage.update(user);
        User updatedUser = userStorage.findById(user.getId());
        assertEquals(user, updatedUser);

        // обновление не существующего объекта
        user.setId(Long.MAX_VALUE);
        assertThrows(InternalServerException.class, () -> userStorage.update(user));
    }

    @Test
    public void deleteAll() {
        userStorage.deleteAll();
        assertTrue(userStorage.findAll().isEmpty());
    }
}

package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.practicum.filmorate.exception.WrongRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = {"app.storage.in-memory=false"})
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FriendDbStorage.class, UserDbStorage.class, UserRowMapper.class})
public class FriendDbStorageTest {
    private final FriendDbStorage friendStorage;
    private final UserDbStorage userStorage;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(null, "email1@mail.ru", "login1", "user1", LocalDate.of(1980, 1, 1), null);
        user1 = userStorage.create(user1);
        user2 = new User(null, "email2@mail.ru", "login2", "user2", LocalDate.of(1980, 1, 2), null);
        user2 = userStorage.create(user2);
        user3 = new User(null, "email3@mail.ru", "login3", "user3", LocalDate.of(1980, 1, 3), null);
        user3 = userStorage.create(user3);
    }

    @Test
    public void addFriend() {
        friendStorage.addFriend(user1.getId(), user2.getId());
        assertEquals(List.of(user2), friendStorage.getFriends(user1.getId()));
        // дружба односторонняя, к user1 добавился друг user2, это не значит, у юзера user2 есть друг user1
        assertNotEquals(List.of(user1), friendStorage.getFriends(user2.getId()));

        friendStorage.addFriend(user1.getId(), user3.getId());
        assertEquals(List.of(user2, user3), friendStorage.getFriends(user1.getId()));
        assertNotEquals(List.of(user1), friendStorage.getFriends(user3.getId()));

        // добавляем друга самого себя, должна быть ошибка
        assertThrows(WrongRequestException.class, () -> friendStorage.addFriend(user1.getId(), user1.getId()));

        // добавляем еще раз этого же друга, должна быть ошибка
        assertThrows(DuplicateKeyException.class, () -> friendStorage.addFriend(user1.getId(), user3.getId()));
    }

    @Test
    public void deleteFriend() {
        // удаляем не существующего юзера, не должно быть исключения, так как проверка в сервисе
        assertDoesNotThrow(() -> friendStorage.deleteFriend(user1.getId(), user2.getId()));

        // удаляем у не существующего юзера друга, не должно быть исключения, так как проверка в сервисе
        assertDoesNotThrow(() -> friendStorage.deleteFriend(Long.MAX_VALUE, user2.getId()));

        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.addFriend(user1.getId(), user3.getId());
        assertEquals(List.of(user2, user3), friendStorage.getFriends(user1.getId()));

        friendStorage.deleteFriend(user1.getId(), user2.getId());
        assertEquals(List.of(user3), friendStorage.getFriends(user1.getId()));

        friendStorage.deleteFriend(user1.getId(), user3.getId());
        assertTrue(friendStorage.getFriends(user1.getId()).isEmpty());
    }

    @Test
    public void getFriends() {
        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.addFriend(user1.getId(), user3.getId());
        assertEquals(List.of(user2, user3), friendStorage.getFriends(user1.getId()));

        // поиск друзей у не существующего юзера, не должно быть исключения, так как проверка в сервисе
        assertDoesNotThrow(() -> friendStorage.getFriends(Long.MAX_VALUE));
    }

    @Test
    public void getCommonFriends() {
        friendStorage.addFriend(user1.getId(), user2.getId());
        friendStorage.addFriend(user1.getId(), user3.getId());
        friendStorage.addFriend(user2.getId(), user3.getId());
        assertEquals(List.of(user3), friendStorage.getCommonFriends(user1.getId(), user2.getId()));

        // поиск общих друзей у не существующих юзеров, не должно быть исключения, так как проверка в сервисе
        assertDoesNotThrow(() -> friendStorage.getCommonFriends(Long.MAX_VALUE, Long.MAX_VALUE - 1));
    }
}

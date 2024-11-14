package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.model.Event.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.Event.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Event.Operation.REMOVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final BaseStorage<User> userStorage;
    private final FriendStorage friendStorage;
    private final EventStorage eventStorage;

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        log.info("Обработан запрос на получение всех пользователей");
        return users;
    }

    public User findById(long id) {
        User user = userStorage.findById(id);
        log.info("Обработан запрос на получение пользователя с ID {}", id);
        return user;
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя с ID: {} заменено логином", user.getId());
        }
        User createdUser = userStorage.create(user);
        log.info("Создан пользователь с ID {}", createdUser.getId());
        return createdUser;
    }

    public User update(User user) {
        User savedUser = userStorage.findById(user.getId());

        if (user.getEmail() != null) savedUser.setEmail(user.getEmail());
        if (user.getName() != null) savedUser.setName(user.getName());
        if (user.getLogin() != null) savedUser.setLogin((user.getLogin()));
        if (user.getBirthday() != null) savedUser.setBirthday((user.getBirthday()));

        userStorage.update(savedUser);
        log.info("Обновлен пользователь с ID {}", savedUser.getId());
        return savedUser;
    }

    public void deleteAll() {
        userStorage.deleteAll();
        log.info("Удалены все пользователи");
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        friendStorage.addFriend(user, friend);
        log.info("Добавлен пользователю с ID {} друг с ID {}", userId, friendId);
        eventStorage.addEvent(userId, FRIEND, ADD, friendId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        friendStorage.deleteFriend(user, friend);
        log.info("Удален у пользователя с ID {} друг с ID {}", userId, friendId);
        eventStorage.addEvent(userId, FRIEND, REMOVE, friendId);
        return user;
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.findById(userId);
        List<User> friends = friendStorage.getFriends(user);
        log.info("Обработан запрос на получение друзей пользователя с ID {}", userId);
        return friends;
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);
        List<User> friends = friendStorage.getCommonFriends(user, other);
        log.info("Обработан запрос на получение общих друзей пользователей с ID {} и ID {}", userId, otherId);
        return friends;
    }

    public void deleteUser(Long id) {
        userStorage.deleteById(id);
        log.info("Пользователь с ID: {} успешно удалён", id);
    }

    public List<Event> getEvents(long id) {
        findById(id);
        List<Event> events = eventStorage.getEvents(id);
        log.info("Обработан запрос на получение события с ID {}", id);
        return events;
    }
}

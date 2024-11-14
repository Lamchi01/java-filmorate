package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.Collection;
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
    private final EventService eventService;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя с ID: {} заменено логином", user.getId());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        User savedUser = userStorage.findById(user.getId());

        if (user.getEmail() != null) savedUser.setEmail(user.getEmail());
        if (user.getName() != null) savedUser.setName(user.getName());
        if (user.getLogin() != null) savedUser.setLogin((user.getLogin()));
        if (user.getBirthday() != null) savedUser.setBirthday((user.getBirthday()));

        userStorage.update(user);
        return savedUser;
    }

    public void deleteAll() {
        userStorage.deleteAll();
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        friendStorage.addFriend(user, friend);
        eventService.addEvent(userId, FRIEND, ADD, friendId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        friendStorage.deleteFriend(user, friend);
        eventService.addEvent(userId, FRIEND, REMOVE, friendId);
        return user;
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.findById(userId);
        return friendStorage.getFriends(user);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);
        return friendStorage.getCommonFriends(user, other);
    }

    public void deleteUser(Long id) {
        userStorage.deleteById(id);
        log.info("Пользователь с ID: {} успешно удалён", id);
    }

    public Collection<Event> getEvents(long id) {
        findById(id);
        return eventService.getEvents(id);
    }
}

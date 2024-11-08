package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final BaseStorage<User> userStorage;
    private final FriendStorage friendStorage;
    private final EventStorage eventStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        if (user.getName() == null) {
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
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.FRIEND)
                .operation(Event.Operation.ADD)
                .entityId(friendId)
                .build();
        eventStorage.addEvent(event);
        log.info("Создано событие добавления friend - {}", event);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        friendStorage.deleteFriend(user, friend);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.FRIEND)
                .operation(Event.Operation.REMOVE)
                .entityId(friendId)
                .build();
        eventStorage.addEvent(event);
        log.info("Создано событие удаления friend - {}", event);
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
        log.trace("Пользователь с ID: {} успешно удалён", id);
    }

    public Collection<Event> getEvents(long id) {
        findById(id);
        return eventStorage.getEvents(id);
    }

}

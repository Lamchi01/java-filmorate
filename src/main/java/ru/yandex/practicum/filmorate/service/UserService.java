package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

//@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) {
        return userStorage.findById(id);
    }

    public void create(User user) {
        user.setId(getNextId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("Пустое имя пользователя с ID: {} заменено логином", user.getId());
        }
        userStorage.create(user);
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

        user.addFriend(friendId);
        //friend.addFriend(userId);
        log.trace("Пользователю с ID: {} добавлен друг с ID: {}", userId, friendId);
        log.trace("Пользователю с ID: {} добавлен друг с ID: {}", friendId, userId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.deleteFriend(friendId);
        //friend.deleteFriend(userId);
        log.trace("У пользователя с ID: {} удален друг с ID: {}", userId, friendId);
        //log.trace("У пользователя с ID: {} удален друг с ID: {}", friendId, userId);
        return user;
    }

    public Collection<User> getFriends(long userId) {
        User user = userStorage.findById(userId);
        Set<Long> friends = user.getFriends();
        log.trace("Получен запрос на получение друзей пользователя с ID: {}", userId);
        return friends.stream().map(userStorage::findById).toList();
    }

    public Collection<User> getCommonFriends(long userId, long otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();
        log.trace("Получен запрос на получение общих другей пользователей с ID: {}, {}", userId, otherId);
        return userFriends.stream().filter(otherFriends::contains).map(userStorage::findById).toList();
    }

    // вспомогательный метод для генерации нового идентификатора
    private long getNextId() {
        long currentMaxId = userStorage.findAll()
                .stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }
}

package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        log.trace("Получен запрос на получение всех пользователей");
        return users.values();
    }

    @Override
    public void create(User user) {
        users.put(user.getId(), user);
        log.trace("Добавлен новый пользователь с ID: {}", user.getId());
    }

    @Override
    public User findById(Long id) {
        log.trace("Получен запрос на получение пользовтале с IDL {}", id);
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с ID: {} не найден", id);
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public void update(User user) {
        if (findById(user.getId()) == null) {
            return;
        }
        users.replace(user.getId(), user);
        log.trace("Обновлен пользователь с ID: {}", user.getId());
    }

    @Override
    public void deleteAll() {
        users.clear();
        log.trace("Удалены все пользователи");
    }

    @Override
    public User addFriend(long userId, long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        if (user == null || friend == null) {
            return null;
        }

        user.getFriends().add(friendId);
        log.trace("Пользователю с ID: {} добавлен друг с ID: {}", userId, friendId);
        friend.getFriends().add(userId);
        log.trace("Пользователю с ID: {} добавлен друг с ID: {}", friendId, userId);
        return user;
    }

    @Override
    public User removeFriend(long userId, long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        if (user == null || friend == null) {
            return null;
        }

        user.getFriends().remove(friendId);
        log.trace("У пользователя с ID: {} удален друг с ID: {}", userId, friendId);
        friend.getFriends().remove(userId);
        log.trace("У пользователя с ID: {} удален друг с ID: {}", friendId, userId);
        return user;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        User user = findById(userId);
        Set<Long> friends = user.getFriends();
        return friends.stream().map(users::get).toList();
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long otherId) {
        User user = findById(userId);
        Set<Long> userFriends = user.getFriends();
        User other = findById(otherId);
        Set<Long> otherFriends = other.getFriends();
        return userFriends.stream().filter(otherFriends::contains).map(users::get).toList();
    }
}

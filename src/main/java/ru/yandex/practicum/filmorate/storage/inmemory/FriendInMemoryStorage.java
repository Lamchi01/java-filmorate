package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "true")
public class FriendInMemoryStorage implements FriendStorage {
    private final BaseStorage<User> userStorage;

    @Override
    public void addFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        user.addFriend(friendId);
        log.trace("Пользователю с ID {} добавлен друг с ID {}", userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.findById(userId);
        user.deleteFriend(friendId);
        log.trace("У пользователя с ID {} удален друг с ID {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        log.trace("Получен запрос на получение всех друзей пользователя с ID {}", userId);
        User user = userStorage.findById(userId);
        Set<Long> friends = user.getFriends();
        if (friends == null) {
            return new ArrayList<>();
        }
        return friends.stream().map(userStorage::findById).toList();
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        log.trace("Получен запрос на получение общий друзей пользователей с ID {} и ID {}", userId, otherId);
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();
        if (userFriends == null || otherFriends == null) {
            return new ArrayList<>();
        }
        return userFriends.stream().filter(otherFriends::contains).map(userStorage::findById).toList();
    }
}

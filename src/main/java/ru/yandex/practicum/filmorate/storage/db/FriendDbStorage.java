package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FriendDbStorage implements FriendStorage {
    protected final JdbcTemplate jdbc;

    @Override
    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new WrongRequestException("Попытка добавить друга самого себя");
        }

        jdbc.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?)", userId, friendId);
        log.trace("Пользователю с ID {} добавлен друг с ID {}", userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        jdbc.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
        log.trace("У пользователя с ID {} удален друг с ID {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        log.trace("Получен запрос на получение всех друзей пользователя с ID {}", userId);
        return jdbc.query("SELECT u.* FROM users u " +
                        "WHERE u.user_id IN (SELECT friend_id FROM friends f WHERE f.user_id = ?)",
                new UserRowMapper(), userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = "SELECT * FROM users WHERE user_id " +
                "IN (SELECT friend_id FROM friends WHERE user_id = ?) " +
                "AND user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        log.trace("Получен запрос на получение общий друзей пользователей с ID {} и ID {}", userId, otherId);
        return jdbc.query(sql, new UserRowMapper(), userId, otherId);
    }
}

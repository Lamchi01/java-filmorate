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
    public void addFriend(User user, User friend) {
        log.info("Добавление пользователю с ID {} друга с ID {}", user.getId(), friend.getId());
        if (user.equals(friend)) {
            throw new WrongRequestException("Попытка добавить друга самого себя");
        }

        jdbc.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?)", user.getId(), friend.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        log.info("Удаление у пользователя с ID {} друга с ID {}", user.getId(), friend.getId());
        jdbc.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", user.getId(), friend.getId());
    }

    @Override
    public List<User> getFriends(User user) {
        log.info("Получение всех друзей пользователя с ID {}", user.getId());
        return jdbc.query("SELECT u.* FROM users u " +
                        "WHERE u.user_id IN (SELECT friend_id FROM friends f WHERE f.user_id = ?)",
                new UserRowMapper(), user.getId());
    }

    @Override
    public List<User> getCommonFriends(User user, User other) {
        log.info("Получение общий друзей пользователей с ID {} и ID {}", user.getId(), other.getId());
        String sql = "SELECT * FROM users WHERE user_id " +
                "IN (SELECT friend_id FROM friends WHERE user_id = ?) " +
                "AND user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        return jdbc.query(sql, new UserRowMapper(), user.getId(), other.getId());
    }
}

package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Slf4j
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "false")
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    protected final JdbcTemplate jdbc;

    @Override
    public void likeFilm(long filmId, long userId) {
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        log.trace("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        jdbc.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        log.trace("Удален лайк пользователя с ID {} к фильму с ID {}", userId, filmId);
    }
}

package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String UPDATE_COUNT_LIKES_QUERY = "UPDATE films " +
            "SET count_likes = count_likes + ? WHERE film_id = ?";
    protected final JdbcTemplate jdbc;

    @Override
    public void likeFilm(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
        updateCountLikes(1, filmId);
        log.trace("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
        updateCountLikes(-1, filmId);
        log.trace("Удален лайк пользователя с ID {} к фильму с ID {}", userId, filmId);
    }

    private void updateCountLikes(int count, long filmId) {
        jdbc.update(UPDATE_COUNT_LIKES_QUERY, count, filmId);
    }
}

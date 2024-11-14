package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private static final String FIND_COUNT_LIKES_BY_ID_QUERY = "SELECT COUNT(user_id) FROM likes WHERE film_id = ? GROUP BY film_id";
    private static final String ADD_LIKE_QUERY = "MERGE INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String UPDATE_COUNT_LIKES_QUERY = "UPDATE films " +
            "SET count_likes = ? WHERE film_id = ?";
    protected final JdbcTemplate jdbc;

    @Override
    public void likeFilm(Film film, User user) {
        log.info("Создание лайка пользователя с ID {} к фильму с ID {}", user.getId(), film.getId());
        jdbc.update(ADD_LIKE_QUERY, film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        log.info("Удаление лайка пользователя с ID {} к фильму с ID {}", user.getId(), film.getId());
        jdbc.update(DELETE_LIKE_QUERY, film.getId(), user.getId());
    }

    @Override
    public long getLikes(Film film) {
        log.info("Получение лаков к фильму с ID {}", film.getId());
        List<Long> likes = jdbc.queryForList(FIND_COUNT_LIKES_BY_ID_QUERY, Long.class, film.getId());
        if (likes.isEmpty()) {
            return 0L;
        }
        return likes.getFirst();
    }

    @Override
    public void updateCountLikes(Film film) {
        long likes = getLikes(film);
        jdbc.update(UPDATE_COUNT_LIKES_QUERY, likes, film.getId());
        film.setCountLikes(likes);
    }
}

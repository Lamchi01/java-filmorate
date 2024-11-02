package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.WrongRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private static final String FIND_COUNT_LIKES_BY_ID_QUERY = "SELECT COUNT(user_id) FROM likes WHERE film_id = ? GROUP BY film_id";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String UPDATE_COUNT_LIKES_QUERY = "UPDATE films " +
            "SET count_likes = count_likes + ? WHERE film_id = ?";
    protected final JdbcTemplate jdbc;

    @Override
    public void likeFilm(Film film, User user) {
        jdbc.update(ADD_LIKE_QUERY, film.getId(), user.getId());
        updateCountLikes(1, film);
        log.trace("Пользователь с ID {} поставил лайк фильму с ID {}", user.getId(), film.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        jdbc.update(DELETE_LIKE_QUERY, film.getId(), user.getId());
        updateCountLikes(-1, film);
        log.trace("Удален лайк пользователя с ID {} к фильму с ID {}", user.getId(), film.getId());
    }

    @Override
    public long getLikes(Film film) {
        Long likes = jdbc.queryForObject(FIND_COUNT_LIKES_BY_ID_QUERY, Long.class, film.getId());
        if (likes == null) {
            throw new WrongRequestException("Ошибка запроса");
        }
        return likes;
    }

    private void updateCountLikes(int count, Film film) {
        jdbc.update(UPDATE_COUNT_LIKES_QUERY, count, film.getId());
        film.setCountLikes(film.getCountLikes() + count);
    }
}
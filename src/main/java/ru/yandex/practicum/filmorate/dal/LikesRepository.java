package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class LikesRepository extends BaseRepository<Film> {
    private static final String INSERT_QUERY_OF_FILM = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY_OF_FILM = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(Integer filmId, Integer userId) {
        update(INSERT_QUERY_OF_FILM, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        update(DELETE_QUERY_OF_FILM, filmId, userId);
    }
}
package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Likes;

import java.util.Collection;
import java.util.HashSet;

@Repository
public class LikesRepository extends BaseRepository<Likes> {
    private static final String QUERY_ALL_LIKES = "SELECT * FROM LIKES";
    private static final String QUERY_LIKES_BY_ID = "SELECT * FROM LIKES WHERE FILM_ID = ?";
    private static final String INSERT_QUERY_OF_FILM = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_QUERY_OF_FILM = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    public LikesRepository(JdbcTemplate jdbc, RowMapper<Likes> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Likes> getAllLikes() {
        return findMany(QUERY_ALL_LIKES);
    }

    public HashSet<Integer> getLikesByFilmId(int filmId) {
        Collection<Likes> likes = findMany(QUERY_LIKES_BY_ID, filmId);
        return new HashSet<>(likes
                .stream()
                .map(Likes::getUserId)
                .toList());
    }

    public void addLike(Integer filmId, Integer userId) {
        update(INSERT_QUERY_OF_FILM, filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        update(DELETE_QUERY_OF_FILM, filmId, userId);
    }
}
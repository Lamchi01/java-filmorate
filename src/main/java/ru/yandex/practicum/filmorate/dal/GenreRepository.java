package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.HashSet;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String QUERY_FOR_ALL_GENRES = "SELECT * FROM GENRE";
    private static final String QUERY_FOR_GENRE_BY_ID = "SELECT * FROM GENRE WHERE ID = ?";
    private static final String QUERY_FOR_GENRES_BY_FILM_ID = "SELECT * FROM GENRE WHERE ID IN" +
            "(SELECT ID FROM GENRES WHERE FILM_ID = ?)";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getAllGenres() {
        return findMany(QUERY_FOR_ALL_GENRES);
    }

    public Genre getGenreById(Integer id) {
        return findOne(QUERY_FOR_GENRE_BY_ID, id);
    }

    public HashSet<Genre> getGenresByFilmId(Integer filmId) {
        Collection<Genre> genres = findMany(QUERY_FOR_GENRES_BY_FILM_ID, filmId);
        return new HashSet<>(genres);
    }
}
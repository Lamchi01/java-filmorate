package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genres;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
public class GenresRepository extends BaseRepository<Genres> {
    private static final String QUERY_FOR_ALL_GENRES = "SELECT * FROM GENRES";
    private static final String INSERT_QUERY = "INSERT INTO GENRES (FILM_ID, ID) VALUES (?, ?)";
    private static final String DELETE_ALL_FROM_FILM_QUERY = "DELETE FROM GENRES WHERE FILM_ID = ?";

    public GenresRepository(JdbcTemplate jdbc, RowMapper<Genres> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genres> getAllGenres() {
        return findMany(QUERY_FOR_ALL_GENRES);
    }

    public void addGenresToFilm(Integer filmId, List<Integer> genresIds) {
            batchUpdateBase(INSERT_QUERY, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genresIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return genresIds.size();
                }
            });
        }

    public void deleteAllGenresFromFilm(Integer filmId) {
        jdbc.update(DELETE_ALL_FROM_FILM_QUERY, filmId);
    }
}
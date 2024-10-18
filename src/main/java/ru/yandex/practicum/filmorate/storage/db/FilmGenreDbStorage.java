package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    protected final JdbcTemplate jdbc;

    @Override
    public List<Genre> getGenres(long filmId) {
        log.trace("Получен запрос на получение жанров фильма с ID {}", filmId);
        return jdbc.query("SELECT * FROM genres WHERE genre_id IN " +
                "(SELECT genre_id FROM film_genres WHERE film_id = ?)", new GenreRowMapper(), filmId);
    }

    @Override
    public void addGenre(long filmId, long genreId) {
        jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", filmId, genreId);
        log.trace("Добавлен жанр с ID {} к фильму с ID {}", genreId, filmId);
    }

    @Override
    public void deleteFilmGenres(long filmId) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
        log.trace("УдалиЛи все жанры у фильма с ID {}", filmId);
    }
}

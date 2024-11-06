package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
    private static final String FIND_BY_DIRECTOR_ID_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN " +
            "(SELECT film_id FROM film_directors WHERE director_id = ?)";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM films";
    private static final String FIND_POPULAR_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
    private static final String FIND_ALL_FILM_GENRES_QUERY = "SELECT fg.*, g.name genre_name FROM film_genres fg " +
            "LEFT JOIN genres g ON fg.genre_id = g.genre_id";
    private static final String FIND_ALL_FILM_DIRECTORS_QUERY = "SELECT fd.*, d.name director_name FROM film_directors fd " +
            "LEFT JOIN directors d ON fd.director_id = d.director_id";
    private static final String FIND_FILMS_GENRES_QUERY = "SELECT fg.*, g.name genre_name " +
            "FROM film_genres fg " +
            "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
            "WHERE fg.film_id IN (%s)";
    private static final String FIND_FILMS_DIRECTORS_QUERY = "SELECT fd.*, d.name director_name " +
            "FROM film_directors fd " +
            "LEFT JOIN directors d ON fd.director_id = d.director_id " +
            "WHERE fd.film_id IN (%s)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM films WHERE film_id = ?";


    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        log.trace("Получен запрос на получение всех фильмов");
        List<Film> films = findMany(FIND_ALL_QUERY);
        Map<Long, LinkedHashSet<Genre>> genres = getAllFilmGenres();
        Map<Long, LinkedHashSet<Director>> directors = getAllFilmDirectors();
        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(new LinkedHashSet<>(genres.get(film.getId())));
            }
            if (directors.containsKey(film.getId())) {
                film.setDirectors(new LinkedHashSet<>(directors.get(film.getId())));
            }
        }
        return films;
    }

    @Override
    public Film findById(Long id) {
        log.trace("Получен запрос на получение фильма с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public Film create(Film film) {
        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        log.trace("Добавлен новый фильм с ID: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        log.trace("Обновлен фильм с ID: {}", film.getId());
        return film;
    }

    @Override
    public void deleteAll() {
        removeAll(DELETE_ALL_QUERY);
        log.trace("Удалены все фильмы");
    }

    @Override
    public List<Film> popularFilms(int count) {
        log.trace("Получен запрос на получение TOP {} популярных фильмов", count);
        List<Film> films = findMany(FIND_POPULAR_QUERY, count);
        Map<Long, LinkedHashSet<Genre>> genres = getFilmsGenres(films);
        Map<Long, LinkedHashSet<Director>> directors = getFilmsDirectors(films);

        for (Film film : films) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(new LinkedHashSet<>(genres.get(film.getId())));
            }
            if (directors.containsKey(film.getId())) {
                film.setDirectors(new LinkedHashSet<>(directors.get(film.getId())));
            }
        }
        return films;
    }

    @Override
    public List<Film> findFilmsByDirectorId(long directorId, String sortedBy) {
        List<Film> directorFilms = findMany(FIND_BY_DIRECTOR_ID_QUERY, directorId);

        Map<Long, LinkedHashSet<Genre>> genres = getAllFilmGenres();
        Map<Long, LinkedHashSet<Director>> directors = getAllFilmDirectors();
        for (Film film : directorFilms) {
            if (genres.containsKey(film.getId())) {
                film.setGenres(new LinkedHashSet<>(genres.get(film.getId())));
            }
            if (directors.containsKey(film.getId())) {
                film.setDirectors(new LinkedHashSet<>(directors.get(film.getId())));
            }
        }

        if (sortedBy.equals("year")) {
            return directorFilms.stream().sorted(Comparator.comparing(Film::getReleaseDate)).toList();
        } else {
            return directorFilms.stream().sorted(Comparator.comparing(Film::getCountLikes).reversed()).toList();
        }
    }

    /**
     * Метод для выборки всех жанров всех фильмов
     *
     * @return - HashSet, ключ- ID фильма, значение - список жанров в виде объектов
     */
    private Map<Long, LinkedHashSet<Genre>> getAllFilmGenres() {
        Map<Long, LinkedHashSet<Genre>> res = new HashMap<>();
        return jdbc.query(FIND_ALL_FILM_GENRES_QUERY, (ResultSet rs) -> {
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long genreId = rs.getLong("genre_id");
                String genreName = rs.getString("genre_name");
                res.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(new Genre(genreId, genreName));
            }
            return res;
        });
    }

    /**
     * Метод для выборки всех режиссёров всех фильмов
     *
     * @return - HashSet, ключ- ID фильма, значение - список режиссёров в виде объектов
     */
    private Map<Long, LinkedHashSet<Director>> getAllFilmDirectors() {
        Map<Long, LinkedHashSet<Director>> res = new HashMap<>();
        return jdbc.query(FIND_ALL_FILM_DIRECTORS_QUERY, (ResultSet rs) -> {
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long directorId = rs.getLong("director_id");
                String directorName = rs.getString("director_name");
                res.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(new Director(directorId, directorName));
            }
            return res;
        });
    }

    /**
     * Метод для выборки жанров фильмов по списку фильмов
     *
     * @param films - список фильмов для выборки
     * @return - HashSet, ключ - ID фильма, значение - список жанров в виде объектов
     */
    private Map<Long, LinkedHashSet<Genre>> getFilmsGenres(List<Film> films) {
        Long[] filmIds = films.stream().map(Film::getId).toArray(Long[]::new);
        String inSql = String.join(",", Collections.nCopies(filmIds.length, "?"));
        Map<Long, LinkedHashSet<Genre>> res = new HashMap<>();
        return jdbc.query(String.format(FIND_FILMS_GENRES_QUERY, inSql),
                filmIds,
                (ResultSet rs) -> {
                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Long genreId = rs.getLong("genre_id");
                        String genreName = rs.getString("genre_name");
                        res.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(new Genre(genreId, genreName));
                    }
                    return res;
                });
    }

    /**
     * Метод для выборки режиссёров фильмов по списку фильмов
     *
     * @param films - список фильмов для выборки
     * @return - HashSet, ключ - ID фильма, значение - список режиссёров в виде объектов
     */
    private Map<Long, LinkedHashSet<Director>> getFilmsDirectors(List<Film> films) {
        Long[] filmIds = films.stream().map(Film::getId).toArray(Long[]::new);
        String inSql = String.join(",", Collections.nCopies(filmIds.length, "?"));
        Map<Long, LinkedHashSet<Director>> res = new HashMap<>();
        return jdbc.query(String.format(FIND_FILMS_DIRECTORS_QUERY, inSql),
                filmIds,
                (ResultSet rs) -> {
                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Long directorId = rs.getLong("director_id");
                        String directorName = rs.getString("director_name");
                        res.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(new Director(directorId, directorName));
                    }
                    return res;
                });
    }


    @Override
    public void deleteById(long id) {
        if (!removeOne(DELETE_BY_ID_QUERY, id)) {
            throw new NotFoundException("Фильм с ID " + id + " не найден.");
        }
        log.trace("Фильм с ID: {} успешно удалён", id);
    }
}

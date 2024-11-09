package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.WrongRequestException;
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
    private static final String FIND_BY_NAME_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.name like ?";
    private static final String FIND_BY_DIRECTOR_ID_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN " +
            "(SELECT film_id FROM film_directors WHERE director_id = ?)";
    private static final String FIND_BY_DIRECTOR_NAME_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN " +
            "(SELECT fd.film_id FROM film_directors fd " +
            "LEFT JOIN directors d ON fd.director_id = d.director_id" +
            " WHERE d.name like ?)";
    private static final String FIND_BY_DIRECTOR_NAME_AND_FILM_NAME_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN " +
            "(SELECT fd.film_id FROM film_directors fd " +
            "LEFT JOIN directors d ON fd.director_id = d.director_id " +
            " WHERE d.name like ?) and f.name like ?";
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
    private static final String FIND_COMMON_FILMS = "SELECT DISTINCT f.FILM_ID,\n" +
            "\tf.NAME, \n" +
            "\tf.DESCRIPTION,\n" +
            "\tf.RELEASE_DATE,\n" +
            "\tf.DURATION,\n" +
            "\tf.MPA_ID,\n" +
            "\tm.NAME AS mpa_name,\n" +
            "\tf.COUNT_LIKES \n" +
            "FROM FILMS f \n" +
            "LEFT JOIN LIKES l1 ON f.FILM_ID = l1.FILM_ID \n" +
            "LEFT JOIN LIKES l2 ON f.FILM_ID = l2.FILM_ID \n" +
            "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID \n" +
            "WHERE l1.USER_ID = ? AND l2.USER_ID = ?\n" +
            "ORDER BY f.COUNT_LIKES desc;";


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

    @Override
    public List<Film> findCommonFilms(long userId, long friendId) {
        List<Film> films = findMany(FIND_COMMON_FILMS, userId, friendId);

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
    public List<Film> findFilms(String query, String by) {
        List<Film> films = new ArrayList<>();
        String[] queryParts = query.split(",");
        String[] byParts = by.split(",");
        if (byParts.length == 1) {
            if (byParts[0].equals("title")) {
                films = findMany(FIND_BY_NAME_QUERY, "%" + query + "%");
            } else if (byParts[0].equals("director")) {
                films = findMany(FIND_BY_DIRECTOR_NAME_QUERY, "%" + query + "%");
            } else {
                throw new WrongRequestException("Указано неверное значение критерия поиска (by) для поиска фильма." +
                        " Значение переданного критерия поиска (by) = " + by);
            }
        } else if (byParts.length == 2) {
            String directorQuery;
            String titleQuery;
            if (byParts[0].equals("title") && byParts[1].equals("director")) {
                directorQuery = queryParts.length == 2 ? queryParts[1] : null;
                titleQuery = queryParts[0];
            } else if (byParts[0].equals("director") && byParts[1].equals("title")) {
                directorQuery = queryParts[0];
                titleQuery = queryParts.length == 2 ? queryParts[1] : null;
            } else {
                throw new WrongRequestException("Указано неверное значение критерия поиска (by) для поиска фильма." +
                        " Значение переданного критерия поиска (by) = " + by);
            }

            if (directorQuery == null && titleQuery != null) {
                films = findMany(FIND_BY_NAME_QUERY, "%" + titleQuery + "%");
                films.addAll(findMany(FIND_BY_DIRECTOR_NAME_QUERY, "%" + queryParts[0] + "%"));
            } else if (titleQuery == null && directorQuery != null) {
                films = findMany(FIND_BY_DIRECTOR_NAME_QUERY, "%" + directorQuery + "%");
                films.addAll(films = findMany(FIND_BY_NAME_QUERY, "%" + queryParts[0] + "%"));
            } else if (directorQuery != null && titleQuery != null) {
                films = findMany(FIND_BY_DIRECTOR_NAME_AND_FILM_NAME_QUERY, "%" + directorQuery + "%", "%" + titleQuery + "%");
            }
        } else {
            throw new WrongRequestException("Указано неверное значение критерия поиска (by) для поиска фильма." +
                    " Значение переданного критерия поиска (by) = " + by);
        }

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
        return films.stream().sorted(Comparator.comparing(Film::getCountLikes)).toList();
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

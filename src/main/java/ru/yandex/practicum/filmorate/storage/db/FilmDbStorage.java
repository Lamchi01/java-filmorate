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
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE LOWER(f.name) like LOWER(?)";
    private static final String FIND_BY_DIRECTOR_ID_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN " +
            "(SELECT film_id FROM film_directors WHERE director_id = ?)";
    private static final String FIND_BY_DIRECTOR_NAME_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN " +
            "(SELECT fd.film_id FROM film_directors fd " +
            "LEFT JOIN directors d ON fd.director_id = d.director_id " +
            "WHERE LOWER(d.name) like LOWER(?))";
    private static final String FIND_BY_DIRECTOR_NAME_AND_FILM_NAME_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN " +
            "(SELECT fd.film_id FROM film_directors fd " +
            "LEFT JOIN directors d ON fd.director_id = d.director_id " +
            "WHERE LOWER(d.name) like LOWER(?)) and LOWER(f.name) like LOWER(?)";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_ALL_QUERY = "DELETE FROM films";
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
    private static final String FIND_COMMON_FILMS = "SELECT DISTINCT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN likes l1 ON f.film_id = l1.film_id " +
            "LEFT JOIN likes l2 ON f.film_id = l2.film_id " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "WHERE l1.user_id = ? AND l2.user_id = ? " +
            "ORDER BY f.count_likes DESC";
    private static final String FIND_INTERSECTION_LIKES_QUERY = "SELECT l.user_id FROM likes l WHERE l.film_id " +
            "IN (SELECT film_id FROM likes WHERE user_id = ?) " +
            "AND l.user_id <> ? " +
            "GROUP BY l.user_id " +
            "ORDER BY COUNT(l.film_id) DESC " +
            "LIMIT ?";
    private static final String FIND_LIKE_FILMS_FROM_USERS_QUERY = "SELECT f.*, m.name mpa_name FROM films f " +
            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "LEFT JOIN likes l ON f.film_id = l.film_id " +
            "WHERE l.user_id IN (%s)";

    private static final String BASE_POPULAR_QUERY =
            "SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS likes_count " +
                    "FROM films f " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id ";

    private static final String GENRE_JOIN_CONDITION = "LEFT JOIN film_genres fg ON f.film_id = fg.film_id ";
    private static final String GENRE_WHERE_CONDITION = "AND fg.genre_id = ? ";
    private static final String YEAR_WHERE_CONDITION = "AND EXTRACT(YEAR FROM f.release_date) = ? ";
    private static final String GROUP_BY_ORDER_CONDITION = "GROUP BY f.film_id ORDER BY likes_count DESC LIMIT ?";

    private static final String BASE_WHERE_CONDITION = "WHERE 1=1 ";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        log.info("Получение всех фильмов");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film findById(Long id) {
        log.info("Получение фильма с ID: {}", id);
        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public Film create(Film film) {
        log.info("Добавление нового фильма");
        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновление фильма с ID: {}", film.getId());
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        return film;
    }

    @Override
    public void deleteAll() {
        log.info("Удаление всех фильмов");
        removeAll(DELETE_ALL_QUERY);
    }

    @Override
    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        log.info("Получение популярных фильмов");
        StringBuilder sql = new StringBuilder(BASE_POPULAR_QUERY);

        sql.append(genreId != null ? GENRE_JOIN_CONDITION : "");
        sql.append(BASE_WHERE_CONDITION);

        if (genreId != null) {
            sql.append(GENRE_WHERE_CONDITION);
        }

        if (year != null) {
            sql.append(YEAR_WHERE_CONDITION);
        }

        sql.append(GROUP_BY_ORDER_CONDITION);

        Object[] params = buildParamsArray(genreId, year, count);

        return findMany(sql.toString(), params);
    }

    private Object[] buildParamsArray(Long genreId, Integer year, int count) {
        if (genreId != null && year != null) {
            return new Object[]{genreId, year, count};
        } else if (genreId != null) {
            return new Object[]{genreId, count};
        } else if (year != null) {
            return new Object[]{year, count};
        } else {
            return new Object[]{count};
        }
    }

    @Override
    public List<Film> findFilmsByDirectorId(long directorId, String sortedBy) {
        List<Film> directorFilms = findMany(FIND_BY_DIRECTOR_ID_QUERY, directorId);

        if (sortedBy.equals("year")) {
            return directorFilms.stream().sorted(Comparator.comparing(Film::getReleaseDate)).toList();
        } else {
            return directorFilms.stream().sorted(Comparator.comparing(Film::getCountLikes).reversed()).toList();
        }
    }

    @Override
    public List<Film> findCommonFilms(long userId, long friendId) {
        log.info("Получение общих фильмов пользователей с ID {} и ID {}", userId, friendId);
        return findMany(FIND_COMMON_FILMS, userId, friendId);
    }

    @Override
    public List<Film> findFilms(String query, String by) {
        log.info("Получение фильмов по запросу {}, {}", query, by);
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
            } else if (directorQuery != null) {
                films = findMany(FIND_BY_DIRECTOR_NAME_AND_FILM_NAME_QUERY, "%" + directorQuery + "%", "%" + titleQuery + "%");
            }
        } else {
            throw new WrongRequestException("Указано неверное значение критерия поиска (by) для поиска фильма." +
                    " Значение переданного критерия поиска (by) = " + by);
        }
        return films.stream().sorted(Comparator.comparing(Film::getCountLikes, Comparator.reverseOrder())).toList();
    }

    /**
     * Метод получения рекомендаций по фильмам для заданного пользователя
     *
     * @param id - ID пользователя, которому нужно получить рекомендации
     * @return - список рекомендованных фильмов
     */
    @Override
    public List<Film> getRecommendation(long id) {
        log.info("Получение списка рекомендованных фильмов для пользователя с ID {}", id);

        // получим список пользователей, у которых максимальное пересечение по лайкам с заданным
        // последний параметр запроса - ограничение выборки
        List<Long> otherUserIds = jdbc.queryForList(FIND_INTERSECTION_LIKES_QUERY, Long.class, id, id, 1);

        if (otherUserIds.isEmpty()) {
            return List.of();
        }

        // получим список фильмов, на которые поставил лайк заданный пользователь
        List<Film> filmsFromUser1 = findMany(String.format(FIND_LIKE_FILMS_FROM_USERS_QUERY, "?"), id);

        // получим список фильмов, на которые поставили лайки найденные пользователи по пересечениям лайков
        String inSql = String.join(",", Collections.nCopies(otherUserIds.size(), "?"));
        List<Film> filmsFromUser2 = findMany(String.format(FIND_LIKE_FILMS_FROM_USERS_QUERY, inSql), otherUserIds.toArray());

        // уберем из списка фильмов найденного пользователя общие фильмы заданного пользователя и вернем результат
        filmsFromUser2.removeAll(filmsFromUser1);

        return filmsFromUser2;
    }

    /**
     * Метод для выборки жанров фильмов по списку фильмов
     *
     * @param films - список фильмов для выборки
     * @return - HashSet, ключ - ID фильма, значение - список жанров в виде объектов
     */
    @Override
    public Map<Long, LinkedHashSet<Genre>> getFilmsGenres(List<Film> films) {
        log.info("Получение жанров фильмов");
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
    @Override
    public Map<Long, LinkedHashSet<Director>> getFilmsDirectors(List<Film> films) {
        log.info("Получение режиссеров фильмов");
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
        log.info("Удаление фильма с ID {}", id);
        if (!removeOne(DELETE_BY_ID_QUERY, id)) {
            throw new NotFoundException("Фильм с ID " + id + " не найден.");
        }
    }

    /**
     * Метод для выборки всех жанров всех фильмов
     *
     * @return - HashSet, ключ- ID фильма, значение - список жанров в виде объектов
     */
    @Override
    public Map<Long, LinkedHashSet<Genre>> getAllFilmGenres() {
        log.info("Получение всех жанров");
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
    @Override
    public Map<Long, LinkedHashSet<Director>> getAllFilmDirectors() {
        log.info("Получение всех режиссеров");
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
}

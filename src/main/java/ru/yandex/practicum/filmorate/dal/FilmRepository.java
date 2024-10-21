package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private static final String QUERY_FOR_ALL_FILMS = "SELECT * FROM FILMS";
    private static final String QUERY_FOR_FILM_BY_ID = "SELECT * FROM FILMS WHERE FILM_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILMS " +
            "(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, " +
            "RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? WHERE FILM_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FILMS WHERE FILM_ID = ?";

    private final LikesRepository likesRepository;
    private final GenresRepository genresRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;


    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper,
                          @Autowired LikesRepository likesRepository,
                          @Autowired GenresRepository genresRepository,
                          @Autowired MpaRepository mpaRepository,
                          @Autowired GenreRepository genreRepository) {
        super(jdbc, mapper);
        this.likesRepository = likesRepository;
        this.genresRepository = genresRepository;
        this.mpaRepository = mpaRepository;
        this.genreRepository = genreRepository;
    }

    public Collection<Film> getFilms() {
        Collection<Film> films = findMany(QUERY_FOR_ALL_FILMS);
        if (!films.isEmpty()) {
            Collection<Genres> genresList = genresRepository.getAllGenres();
            Collection<Genre> genres = genreRepository.getAllGenres();
            Collection<Likes> likes = likesRepository.getAllLikes();
            Collection<Mpa> mpas = mpaRepository.getAllMpa();

            for (Film film : films) {
                film.getGenres().addAll(genresList
                        .stream()
                        .filter(genre -> genre.getFilmId().equals(film.getId()))
                        .map(genre1 -> new Genre(genre1.getId(), genres
                                .stream()
                                .filter(genre2 -> genre2.getId().equals(genre1.getId()))
                                .findFirst()
                                .get()
                                .getName()))
                        .toList());
                film.getLikes().addAll(likes
                        .stream()
                        .filter(likes1 -> likes1.getFilmId().equals(film.getId()))
                        .map(Likes::getUserId)
                        .toList());
                film.getMpa().setName(mpas.stream()
                        .filter(mpa -> mpa.getId().equals(film.getMpa().getId()))
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Рейтинг с id " + film.getMpa().getId() + " не найден"))
                        .getName());
            }
        }
        return films;
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = findOne(QUERY_FOR_FILM_BY_ID, id);
        if (film != null) {
            Collection<Genre> genres = genreRepository.getAllGenres();
            film.getGenres().addAll(genreRepository
                    .getGenresByFilmId(film.getId())
                    .stream()
                    .map(genre -> new Genre(genre.getId(), genres
                            .stream()
                            .filter(genre1 -> genre1.getId().equals(genre.getId()))
                            .findFirst()
                            .get()
                            .getName()))
                    .toList());
            film.getLikes().addAll(likesRepository.getLikesByFilmId(film.getId()));
            film.getMpa().setName(mpaRepository.getAllMpa()
                    .stream()
                    .filter(mpa -> mpa.getId().equals(film.getMpa().getId()))
                    .findFirst()
                    .get()
                    .getName());
        }
        return film;
    }

    @Override
    public Collection<Film> getTopFilms(Integer count) {
        return getFilms().stream()
                .sorted(Comparator.comparing(f -> f.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .toList();
    }

    @Override
    public Film create(Film film) {
        Integer id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        if (!film.getGenres().isEmpty()) {
            genresRepository.addGenresToFilm(id, film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (!film.getGenres().isEmpty()) {
            genresRepository.deleteAllGenresFromFilm(film.getId());
            genresRepository.addGenresToFilm(film.getId(), film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        return film;
    }

    @Override
    public void delete(Integer id) {
        delete(DELETE_QUERY, id);
    }

    public void addLike(Integer filmId, Integer userId) {
        likesRepository.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likesRepository.deleteLike(filmId, userId);
    }
}
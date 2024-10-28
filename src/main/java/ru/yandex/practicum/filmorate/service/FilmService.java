package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikesRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreRepository genreRepository;
    private final LikesRepository likesRepository;

    public FilmService(@Autowired @Qualifier("filmRepository") FilmStorage filmStorage,
                       @Autowired GenreRepository genreRepository,
                       @Autowired LikesRepository likesRepository) {
        this.filmStorage = filmStorage;
        this.genreRepository = genreRepository;
        this.likesRepository = likesRepository;
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        likesRepository.addLike(filmId, userId);
        log.info("User {} liked film {}", userId, filmId);
        return film;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        likesRepository.deleteLike(filmId, userId);
        log.info("User {} unliked film {}", userId, filmId);
        return film;
    }

    public Collection<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        Film createdFilm = filmStorage.create(film);
        if (!createdFilm.getGenres().isEmpty()) {
            genreRepository.addGenres(createdFilm.getId(), createdFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        return createdFilm;
    }

    public Film update(Film film) {
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException("Не передан идентификатор фильма");
        }
        Film updatedFilm = filmStorage.update(film);
        if (!updatedFilm.getGenres().isEmpty()) {
            genreRepository.deleteGenres(updatedFilm.getId());
            genreRepository.addGenres(updatedFilm.getId(), updatedFilm.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList());
        }
        return updatedFilm;
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }
}
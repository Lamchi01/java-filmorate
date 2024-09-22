package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(int filmId, int userId) {
        userStorage.getUserById(userId); //Для выброса исключения, т.к неизвестный айди пользователя
                                         // не может добавить лайк фильму
        filmStorage.getFilmById(filmId).getLikes().add(userId);
        log.info("User {} liked film {}", userId, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        userStorage.getUserById(userId); //Для выброса исключения, т.к неизвестный айди пользователя
                                         // не может удалить лайк у фильма
        filmStorage.getFilmById(filmId).getLikes().remove(userId);
        log.info("User {} unliked film {}", userId, filmId);
    }

    public Collection<Film> getPopularFilmsByLikes(int count) {
        return filmStorage.getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .toList();
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film updatedFilm) {
        return filmStorage.update(updatedFilm);
    }

    public void delete(int id) {
        filmStorage.delete(id);
    }
}
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public void create(Film film) {
        film.setId(getNextId());
        filmStorage.create(film);
    }

    public Film update(Film film) {
        Film savedFilm = filmStorage.findById(film.getId());

        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getDescription() != null) savedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) savedFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != null) savedFilm.setDuration(film.getDuration());
        if (film.getName() != null) savedFilm.setName(film.getName());

        filmStorage.update(film);
        return savedFilm;
    }

    public void deleteAllFilms() {
        filmStorage.deleteAll();
    }

    public Film likeFilm(long id, long userId) {
        Film film = filmStorage.findById(id);
        userStorage.findById(userId);
        film.addLike(userId);
        log.trace("Добавлен лайк к фильму с ID: {} пользователем с ID: {}", id, userId);
        return film;
    }

    public Film deleteLike(long id, long userId) {
        Film film = filmStorage.findById(id);
        userStorage.findById(userId);
        film.deleteLike(userId);
        log.trace("Удален лайк к фильму с ID: {} пользователя с ID: {}", id, userId);
        return film;
    }

    public List<Film> popularFilms(int count) {
        log.trace("Получен запрос на получение {} популярных фильмов", count);
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparing(Film::getCountLikes).reversed())
                .limit(count)
                .toList();
    }

    // вспомогательный метод для генерации нового идентификатора
    private long getNextId() {
        long currentMaxId = filmStorage.findAll()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }
}

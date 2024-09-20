package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(final UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAll() {
        log.trace("Получен запрос на получение всех фильмов");
        return films.values();
    }

    @Override
    public void create(Film film) {
        films.put(film.getId(), film);
        log.trace("Добавлен новый фильм с ID: {}", film.getId());
    }

    @Override
    public Film findById(Long id) {
        log.trace("Получен запрос на получение фильма с ID: {}", id);
        Film film = films.get(id);
        if (film == null) {
            log.warn("Фильм с ID: {} не найден", id);
            throw new NotFoundException("Фильм с ID: " + id + " не найден");
        }
        return film;
    }

    @Override
    public void update(Film film) {
        if (findById(film.getId()) == null) {
            return;
        }
        films.replace(film.getId(), film);
        log.trace("Обновлен фильм с ID: {}", film.getId());
    }

    @Override
    public void deleteAll() {
        log.trace("Удалены все фильмы");
        films.clear();
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = userStorage.findById(userId);
        if (film == null || user == null) {
            return null;
        }
        Set<Long> likes = film.getLikes();
        if (!likes.contains(userId)) {
            log.trace("Добавлен лайк к фильму с ID: {} пользователем с ID: {}", filmId, userId);
            likes.add(userId);
            film.setCountLikes(film.getCountLikes() + 1);
        } else {
            log.warn("Лайк к фильму с ID: {} пользователем с ID: {} уже поставлен", filmId, userId);
        }
        return film;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        User user = userStorage.findById(userId);
        if (film == null || user == null) {
            return null;
        }
        Set<Long> likes = film.getLikes();
        if (likes.contains(userId)) {
            log.trace("Удален лайк к фильму с ID: {} пользователя с ID: {}", filmId, userId);
            likes.remove(userId);
            film.setCountLikes(film.getCountLikes() - 1);
        } else {
            log.warn("Лайк к фильму с ID: {} пользователем с ID: {} отсутствует", filmId, userId);
        }
        return film;
    }

    @Override
    public List<Film> popularFilms(int count) {
        return findAll()
                .stream()
                .sorted(Comparator.comparing(Film::getCountLikes).reversed())
                .limit(count)
                .toList();
    }
}

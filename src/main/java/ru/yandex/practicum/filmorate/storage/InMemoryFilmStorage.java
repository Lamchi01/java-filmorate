package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

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
        films.replace(film.getId(), film);
        log.trace("Обновлен фильм с ID: {}", film.getId());
    }

    @Override
    public void deleteAll() {
        films.clear();
        log.trace("Удалены все фильмы");
    }
}

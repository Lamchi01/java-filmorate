package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "true")
public class FilmInMemoryStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        log.trace("Получен запрос на получение всех фильмов");
        return films.values().stream().toList();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.trace("Добавлен новый фильм с ID: {}", film.getId());
        return film;
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
    public Film update(Film film) {
        films.replace(film.getId(), film);
        log.trace("Обновлен фильм с ID: {}", film.getId());
        return film;
    }

    @Override
    public void deleteAll() {
        films.clear();
        log.trace("Удалены все фильмы");
    }

    @Override
    public List<Film> popularFilms(int count) {
        log.trace("Получен запрос на получение TOP {} популярных фильмов", count);
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getCountLikes).reversed())
                .limit(count)
                .toList();
    }

    // вспомогательный метод для генерации нового идентификатора
    private long getNextId() {
        long currentMaxId = films.values()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }

}

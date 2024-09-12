package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validator.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.trace("Выполнен запрос на получение всех фильмов");
        return films.values();
    }

    @Validated({Marker.OnCreate.class})
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.trace("Добавлен новый фильм с ID: {}", film.getId());
        return film;
    }

    @Validated(Marker.OnUpdate.class)
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Long id = film.getId();
        if (id == null) {
            log.warn("Должен быть указан ID фильма");
            throw new ValidationException("Должен быть указан ID фильма");
        }

        Film savedFilm = films.get(id);
        if (savedFilm == null) {
            log.warn("Фильм с ID: {} не найден", id);
            throw new ValidationException("Фильм с ID: " + id + " не найден");
        }

        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getDescription() != null) savedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) savedFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != null) savedFilm.setDuration(film.getDuration());
        if (film.getName() != null) savedFilm.setName(film.getName());

        films.replace(id, film);
        log.trace("Обновлен фильм с ID: {}", id);
        return savedFilm;
    }

    @DeleteMapping
    public void deleteAllFilms() {
        log.trace("Удалены все фильмы");
        films.clear();
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

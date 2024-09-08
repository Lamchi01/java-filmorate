package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(getNextId(), film);
        log.info("Film created: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updatedFilm) {
        validateFilm(updatedFilm);
        if (updatedFilm.getId() == null || updatedFilm.getId() <= 0) {
            throw new ValidationException("Id фильма должно быть указано");
        }
        if (!films.containsKey(updatedFilm.getId())) {
            throw new ValidationException("Фильм с указанным id не найден");
        }
        Film oldFilm = films.get(updatedFilm.getId());
        oldFilm.setName(updatedFilm.getName());
        oldFilm.setDescription(updatedFilm.getDescription());
        oldFilm.setReleaseDate(updatedFilm.getReleaseDate());
        oldFilm.setDuration(updatedFilm.getDuration());
        log.info("Film updated: {}", oldFilm);
        return oldFilm;
    }

    private void validateFilm(Film film) {
        LocalDate startFilmsDate = LocalDate.of(1895, 12, 28);
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не должно быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Описание должно быть не пустым и не превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(startFilmsDate)) {
            throw new ValidationException("Дата выхода фильма не может быть раньше 1895 года декабря 28");
        }
        if (film.getDuration() == null || film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательным числом");
        }
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(getNextId(), film);
        log.info("Film created: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updatedFilm) {
        if (updatedFilm.getId() == null || updatedFilm.getId() <= 0) {
            throw new ValidationException("Id фильма должно быть указано");
        }
        if (!films.containsKey(updatedFilm.getId())) {
            throw new ValidationException("Фильм с указанным id не найден");
        }
        Film oldFilm = films.get(updatedFilm.getId());
        if (updatedFilm.getName() != null && !updatedFilm.getName().isBlank()) {
            oldFilm.setName(updatedFilm.getName());
        }
        if (updatedFilm.getDescription() != null && !updatedFilm.getDescription().isBlank()) {
            if (updatedFilm.getDescription().length() > 200) {
                throw new ValidationException("Длина описания не может превышать 200 символов");
            }
            oldFilm.setDescription(updatedFilm.getDescription());
        }
        if (updatedFilm.getReleaseDate() != null) {
            if (updatedFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Фильм не может быть раньше даты рождения фильмов");
            }
            oldFilm.setReleaseDate(updatedFilm.getReleaseDate()); // Добавил проверку на дату выпуска
        }
        if (updatedFilm.getDuration() != 0) {
            oldFilm.setDuration(updatedFilm.getDuration());
        }
        log.info("Film updated: {}", oldFilm);
        return oldFilm;
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
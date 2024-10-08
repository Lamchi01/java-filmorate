package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с указанным id не найден");
        }
        return films.get(id);
    }

    @Override
    public Film create(@Valid Film film) {
        film.setId(getNextId());
        films.put(getNextId(), film);
        log.info("Film created: {}", film);
        return film;
    }

    @Override
    public Film update(Film updatedFilm) {
        if (updatedFilm.getId() == null || updatedFilm.getId() <= 0) {
            throw new ValidationException("Id фильма должно быть указано");
        }
        if (!films.containsKey(updatedFilm.getId())) {
            throw new NotFoundException("Фильм с указанным id не найден");
        }
        Film oldFilm = films.get(updatedFilm.getId());
        if (updatedFilm.getName() != null && !updatedFilm.getName().isBlank()) {
            oldFilm.setName(updatedFilm.getName());
            log.debug("Film name updated: {}", updatedFilm.getName());
        }
        if (updatedFilm.getDescription() != null && !updatedFilm.getDescription().isBlank()) {
            if (updatedFilm.getDescription().length() > 200) {
                throw new ValidationException("Длина описания не может превышать 200 символов");
            }
            oldFilm.setDescription(updatedFilm.getDescription());
            log.debug("Film description updated: {}", updatedFilm.getDescription());
        }
        if (updatedFilm.getReleaseDate() != null) {
            if (updatedFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Фильм не может быть раньше даты рождения фильмов");
            }
            oldFilm.setReleaseDate(updatedFilm.getReleaseDate()); // Добавил проверку на дату выпуска
            log.debug("Film release date updated: {}", updatedFilm.getReleaseDate());
        }
        if (updatedFilm.getDuration() != 0) {
            oldFilm.setDuration(updatedFilm.getDuration());
            log.debug("Film duration updated: {}", updatedFilm.getDuration());
        }
        log.info("Film updated: {}", oldFilm);
        return oldFilm;
    }

    @Override
    public void delete(Integer id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с указанным id не найден");
        }
        films.remove(id);
        log.info("Film deleted: {}", id);
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
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    public final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.storage = filmStorage;
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Film findById(Long id) {
        return storage.findById(id);
    }

    public void create(Film film) {
        film.setId(getNextId());
        storage.create(film);
    }

    public Film update(Film film) {
        Long id = film.getId();
        Film savedFilm = storage.findById(id);
        if (savedFilm == null) {
            log.warn("Фильм с ID: {} не найден", id);
            throw new ValidationException("Фильм с ID: " + id + " не найден");
        }

        if (film.getName() != null) savedFilm.setName(film.getName());
        if (film.getDescription() != null) savedFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) savedFilm.setReleaseDate(film.getReleaseDate());
        if (film.getDuration() != null) savedFilm.setDuration(film.getDuration());
        if (film.getName() != null) savedFilm.setName(film.getName());

        storage.update(film);
        return savedFilm;
    }

    public void deleteAllFilms() {
        storage.deleteAll();
    }

    public Film likeFilm(long id, long userId) {
        return storage.addLike(id, userId);
    }

    public Film deleteLike(long id, long userId) {
        return storage.deleteLike(id, userId);
    }

    public List<Film> popularFilms(int count) {
        return storage.popularFilms(count);
    }

    // вспомогательный метод для генерации нового идентификатора
    private long getNextId() {
        long currentMaxId = storage.findAll()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }
}

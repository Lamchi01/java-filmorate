package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Integer, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        genres.put(1, new Genre(1, "Комедия"));
        genres.put(2, new Genre(2, "Драма"));
        genres.put(3, new Genre(3, "Мультфильм"));
        genres.put(4, new Genre(4, "Триллер"));
        genres.put(5, new Genre(5, "Документальный"));
        genres.put(6, new Genre(6, "Боевик"));
    }

    @Override
    public List<Genre> findAll() {
        log.trace("Получен запрос на получение всех жанров");
        return genres.values().stream().toList();
    }

    @Override
    public void create(Genre genre) {
        genres.put(genre.getId(), genre);
        log.trace("Добавлен новый жанр с ID: {}", genre.getId());
    }

    @Override
    public Genre findById(Integer id) {
        log.trace("Получен запрос на получение жанра с ID: {}", id);
        Genre genre = genres.get(id);
        if (genre == null) {
            log.warn("Жанр с ID: {} не найден", id);
            throw new NotFoundException("Жанр с ID: " + id + " не найден");
        }
        return genre;
    }

    @Override
    public void update(Genre genre) {
        genres.replace(genre.getId(), genre);
        log.trace("Обновлен жанр с ID: {}", genre.getId());
    }

    @Override
    public void deleteAll() {
        genres.clear();
        log.trace("Удалены все жанры");
    }
}

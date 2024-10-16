package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "true")
public class GenreInMemoryStorage implements BaseStorage<Genre> {
    private final Map<Long, Genre> genres = new HashMap<>();

    public GenreInMemoryStorage() {
        genres.put(1L, new Genre(1L, "Комедия"));
        genres.put(2L, new Genre(2L, "Драма"));
        genres.put(3L, new Genre(3L, "Мультфильм"));
        genres.put(4L, new Genre(4L, "Триллер"));
        genres.put(5L, new Genre(5L, "Документальный"));
        genres.put(6L, new Genre(6L, "Боевик"));
    }

    @Override
    public List<Genre> findAll() {
        log.trace("Получен запрос на получение всех жанров");
        return genres.values().stream().toList();
    }

    @Override
    public Genre create(Genre genre) {
        genre.setId(getNextId());
        genres.put(genre.getId(), genre);
        log.trace("Добавлен новый жанр с ID: {}", genre.getId());
        return genre;
    }

    @Override
    public Genre findById(Long id) {
        log.trace("Получен запрос на получение жанра с ID: {}", id);
        Genre genre = genres.get(id);
        if (genre == null) {
            log.warn("Жанр с ID: {} не найден", id);
            throw new NotFoundException("Жанр с ID: " + id + " не найден");
        }
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        genres.replace(genre.getId(), genre);
        log.trace("Обновлен жанр с ID: {}", genre.getId());
        return genre;
    }

    @Override
    public void deleteAll() {
        genres.clear();
        log.trace("Удалены все жанры");
    }

    // вспомогательный метод для генерации нового идентификатора
    private Long getNextId() {
        Long currentMaxId = genres.values()
                .stream()
                .mapToLong(Genre::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }

}

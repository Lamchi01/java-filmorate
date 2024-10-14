package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("inMemoryGenreStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(Integer id) {
        return genreStorage.findById(id);
    }

    public void create(Genre genre) {
        genre.setId(getNextId());
        genreStorage.create(genre);
    }

    public Genre update(Genre genre) {
        Genre savedGenre = genreStorage.findById(genre.getId());
        if (genre.getName() != null) savedGenre.setName(genre.getName());
        genreStorage.update(genre);
        return savedGenre;
    }

    public void deleteAllGenres() {
        genreStorage.deleteAll();
    }

    // вспомогательный метод для генерации нового идентификатора
    private Integer getNextId() {
        Integer currentMaxId = genreStorage.findAll()
                .stream()
                .mapToInt(Genre::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }
}

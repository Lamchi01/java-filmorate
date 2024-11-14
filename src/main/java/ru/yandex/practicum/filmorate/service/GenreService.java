package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {
    private final BaseStorage<Genre> genreStorage;

    public List<Genre> findAll() {
        List<Genre> genres = genreStorage.findAll();
        log.info("Обработан запрос на получение всех жанров");
        return genres;
    }

    public Genre findById(Long id) {
        Genre genre = genreStorage.findById(id);
        log.info("Обработан запрос на получение жанра с ID {}", id);
        return genre;
    }

    public Genre create(Genre genre) {
        Genre gen = genreStorage.create(genre);
        log.info("Создан жанр с ID {}", gen.getId());
        return gen;
    }

    public Genre update(Genre genre) {
        Genre savedGenre = genreStorage.findById(genre.getId());
        if (genre.getName() != null) savedGenre.setName(genre.getName());
        genreStorage.update(savedGenre);
        log.info("Обновлен жанр с ID {}", genre.getId());
        return savedGenre;
    }

    public void deleteAllGenres() {
        genreStorage.deleteAll();
    }
}

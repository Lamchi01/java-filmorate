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
        return genreStorage.findAll();
    }

    public Genre findById(Long id) {
        return genreStorage.findById(id);
    }

    public Genre create(Genre genre) {
        return genreStorage.create(genre);
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
}

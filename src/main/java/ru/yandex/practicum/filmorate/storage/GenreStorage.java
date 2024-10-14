package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface GenreStorage {
    List<Genre> findAll();

    void create(Genre genre);

    Genre findById(Integer id);

    void update(Genre genre);

    void deleteAll();
}

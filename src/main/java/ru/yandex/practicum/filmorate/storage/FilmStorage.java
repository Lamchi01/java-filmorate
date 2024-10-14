package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    void create(Film film);

    Film findById(Long id);

    void update(Film film);

    void deleteAll();
}

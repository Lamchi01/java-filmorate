package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> findAll();

    void create(Mpa mpa);

    Mpa findById(Integer id);

    void update(Mpa mpa);

    void deleteAll();
}

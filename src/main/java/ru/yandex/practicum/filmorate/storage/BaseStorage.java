package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface BaseStorage<T> {
    List<T> findAll();

    T findById(Long id);

    T create(T obj);

    T update(T obj);

    void deleteAll();
}

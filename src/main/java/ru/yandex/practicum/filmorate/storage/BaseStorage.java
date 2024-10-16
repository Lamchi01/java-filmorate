package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface BaseStorage<T> {
    List<T> findAll();

    T create(T obj);

    T findById(Long id);

    T update(T obj);

    void deleteAll();
}

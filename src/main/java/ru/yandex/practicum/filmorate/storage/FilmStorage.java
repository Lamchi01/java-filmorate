package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> findAll();

    void create(Film film);

    Film findById(Long id);

    void update(Film film);

    void deleteAll();

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    List<Film> popularFilms(int count);

}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage extends BaseStorage<Director> {
    List<Director> getDirectors(Film film);

    void addDirectors(Film film, List<Director> director);

    void deleteFilmDirectors(Film film);

    void deleteById(long id);


}

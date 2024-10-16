package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "true")
public class FilmGenreImMemoryStorage implements FilmGenreStorage {
    private final FilmStorage filmStorage;
    private final BaseStorage<Genre> genreStorage;

    @Override
    public List<Genre> getGenres(long filmId) {
        Film film = filmStorage.findById(filmId);
        log.trace("Получен запрос на получение жарнов фильма с ID {}", filmId);
        return film.getGenres();
    }

    @Override
    public void addGenre(long filmId, long genreId) {
        Film film = filmStorage.findById(filmId);
        Genre genre = genreStorage.findById(genreId);
        if (!film.getGenres().contains(genre)) {
            film.getGenres().add(genre);
        }
        log.trace("Добавлен жанр с ID {} к фильму с ID {}", genreId, filmId);
    }

    @Override
    public void deleteFilmGenres(long filmId) {
        Film film = filmStorage.findById(filmId);
        film.setGenres(new ArrayList<>());
    }
}

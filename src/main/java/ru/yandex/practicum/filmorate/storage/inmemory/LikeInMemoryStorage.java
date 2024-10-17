package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "true")
public class LikeInMemoryStorage implements LikeStorage {
    private final FilmStorage filmStorage;

    @Override
    public void likeFilm(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.addLike(userId);
        log.trace("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        film.deleteLike(userId);
        log.trace("Удален лайк пользователя с ID {} к фильму с ID {}", userId, filmId);
    }
}

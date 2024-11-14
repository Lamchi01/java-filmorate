package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.WrongRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmFilters;
import ru.yandex.practicum.filmorate.model.enums.FilmSortOrder;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        log.info("Получен запрос на получение фильма с ID {}", id);
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма");
        filmService.create(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма с ID {}", film.getId());
        return filmService.update(film);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        log.info("Получен запрос на удаление всех фильмов");
        filmService.deleteAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос на лайк фильма с ID {} пользователем с ID {}", id, userId);
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable long id, @PathVariable long userId) {
        log.info("Получен запрос на дизлайк фильма с ID {} пользователем с ID {}", id, userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> popularFilms(@RequestParam(defaultValue = "10") @Positive @NotNull Integer count,
                                   @RequestParam(required = false) Long genreId,
                                   @RequestParam(required = false) Integer year) {
        log.info("Получен запрос на вывод популярных фильмов");
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findByDirectorId(@PathVariable long directorId, @RequestParam(defaultValue = "year") String sortBy) {
        if (FilmSortOrder.from(sortBy) == null) {
            throw new WrongRequestException("Указано неверное значение условия сортировки ответа (sortedBy)");
        }
        log.info("Получен запрос на получение фильмов по режиссеру с ID {} и сортировкой по {}", directorId, sortBy);
        return filmService.findFilmsByDirectorId(directorId, sortBy);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long id) {
        log.info("Получен запрос на удаление фильма с ID {}", id);
        filmService.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> findCommonFilms(@RequestParam @Positive @NotNull long userId,
                                      @RequestParam @Positive @NotNull long friendId) {
        log.info("Получен запрос на получение общих фильмов пользователей с ID {} и ID {}", userId, friendId);
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> findFilms(@RequestParam String query,
                                @RequestParam String by) {
        if (FilmFilters.from(by) == null) {
            throw new WrongRequestException("Указано неверное значение критерия поиска (by)");
        }
        log.info("Получен запрос на получение фильмов по запросу {}, {}", query, by);
        return filmService.findFilms(query, by);
    }
}

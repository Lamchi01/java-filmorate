package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        filmService.create(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @DeleteMapping
    public void deleteAllFilms() {
        filmService.deleteAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.deleteLike(id, userId);
    }


    @GetMapping("/popular")
    public List<Film> popularFilms(@RequestParam(defaultValue = "10") @Positive @NotNull Integer count,
                                   @RequestParam(required = false) Long genreId,
                                   @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findByDirectorId(@PathVariable long directorId, @RequestParam(defaultValue = "year") String sortBy) {
        if (FilmSortOrder.from(sortBy) == null) {
            throw new WrongRequestException("Указано неверное значение условия сортировки ответа (sortedBy)");
        }
        return filmService.findFilmsByDirectorId(directorId, sortBy);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> findCommonFilms(@RequestParam @Positive @NotNull long userId,
                                      @RequestParam @Positive @NotNull long friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> findFilms(@RequestParam String query,
                                @RequestParam String by) {
        if (FilmFilters.from(by) == null) {
            throw new WrongRequestException("Указано неверное значение критерия поиска (by)");
        }
        return filmService.findFilms(query, by);
    }
}

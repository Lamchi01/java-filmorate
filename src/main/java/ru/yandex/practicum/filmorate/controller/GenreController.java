package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<Genre> findAll() {
        log.info("Получен запрос на получение всех жанров");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable long id) {
        log.info("Получен запрос на получение жанра с ID {}", id);
        return genreService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre createGenre(@Valid @RequestBody Genre genre) {
        log.info("Получен запрос на создание жанра");
        genreService.create(genre);
        return genre;
    }

    @PutMapping
    public Genre updateGenre(@Valid @RequestBody Genre genre) {
        log.info("Получен запрос на обновление жанра с ID {}", genre.getId());
        return genreService.update(genre);
    }

    @DeleteMapping
    public void deleteAllGenres() {
        log.info("Получен запрос на удаление всех жанров");
        genreService.deleteAllGenres();
    }
}

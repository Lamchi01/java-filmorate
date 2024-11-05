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
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre findById(@PathVariable long id) {
        return genreService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre createGenre(@Valid @RequestBody Genre genre) {
        genreService.create(genre);
        return genre;
    }

    @PutMapping
    public Genre updateGenre(@Valid @RequestBody Genre genre) {
        return genreService.update(genre);
    }

    @DeleteMapping
    public void deleteAllGenres() {
        genreService.deleteAllGenres();
    }
}

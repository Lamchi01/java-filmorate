package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        log.info("Получен запрос на получение всех режиссёров");
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable long id) {
        log.info("Получен запрос на получение режиссёра с ID: {}", id);
        return directorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("Получен запрос на создание режиссёра");
        return directorService.create(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("Получен запрос на обновление режиссёра с ID: {}", director.getId());
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        log.info("Получен запрос на удаление режиссёра с ID: {}", id);
        directorService.deleteById(id);
    }
}

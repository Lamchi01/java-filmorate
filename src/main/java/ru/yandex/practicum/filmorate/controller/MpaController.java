package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> findAll() {
        log.info("Получен запрос на получение всех рейтингов MPA");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable long id) {
        log.info("Получен запрос на получение рейтинга MPA с ID {}", id);
        return mpaService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa createMpa(@Valid @RequestBody Mpa mpa) {
        log.info("Получен запрос на создание рейтинга MPA");
        mpaService.create(mpa);
        return mpa;
    }

    @PutMapping
    public Mpa updateMpa(@Valid @RequestBody Mpa mpa) {
        log.info("Получен запрос на обновление рейтинга с ID {}", mpa.getId());
        return mpaService.update(mpa);
    }

    @DeleteMapping
    public void deleteAllMpa() {
        log.info("Получен запрос на удаление всех рейтингов MPA");
        mpaService.deleteAllMpa();
    }
}

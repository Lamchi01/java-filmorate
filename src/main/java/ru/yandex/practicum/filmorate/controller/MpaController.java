package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.validator.Marker;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RequiredArgsConstructor
@Validated
@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable int id) {
        return mpaService.findById(id);
    }

    @Validated({Marker.OnCreate.class})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa createMpa(@Valid @RequestBody Mpa mpa) {
        mpaService.create(mpa);
        return mpa;
    }

    @Validated(Marker.OnUpdate.class)
    @PutMapping
    public Mpa updateMpa(@Valid @RequestBody Mpa mpa) {
        return mpaService.update(mpa);
    }

    @DeleteMapping
    public void deleteAllMpa() {
        mpaService.deleteAllMpa();
    }
}

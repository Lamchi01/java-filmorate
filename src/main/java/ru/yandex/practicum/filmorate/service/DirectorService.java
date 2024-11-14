package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> findAll() {
        List<Director> directors = directorStorage.findAll();
        log.info("Обработан запрос на получение всех режиссёров");
        return directors;
    }

    public Director findById(Long id) {
        Director director = directorStorage.findById(id);
        log.info("Обработан запрос на получение режиссёра с ID: {}", id);
        return director;
    }

    public Director create(Director director) {
        Director dir = directorStorage.create(director);
        log.info("Добавлен новый режиссёр с ID: {}", director.getId());
        return dir;
    }

    public Director update(Director director) {
        Director savedDirector = directorStorage.findById(director.getId());
        if (director.getName() != null) savedDirector.setName(director.getName());
        directorStorage.update(savedDirector);
        log.info("Обновлен режиссёр с ID: {}", savedDirector.getId());
        return savedDirector;
    }

    public void deleteAll() {
        directorStorage.deleteAll();
        log.info("Удалены все режиссёры");
    }

    public void deleteById(long id) {
        directorStorage.deleteById(id);
        log.info("Режиссёр с ID: {} успешно удалён", id);
    }
}

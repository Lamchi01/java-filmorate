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
        return directorStorage.findAll();
    }

    public Director findById(Long id) {
        return directorStorage.findById(id);
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        Director savedDirector = directorStorage.findById(director.getId());
        if (director.getName() != null) savedDirector.setName(director.getName());
        directorStorage.update(director);
        return savedDirector;
    }

    public void deleteAll() {
        directorStorage.deleteAll();
    }

    public void deleteById(long id) {
        directorStorage.deleteById(id);
    }

}

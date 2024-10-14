package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(@Qualifier("inMemoryMpaStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(Integer id) {
        return mpaStorage.findById(id);
    }

    public void create(Mpa mpa) {
        mpa.setId(getNextId());
        mpaStorage.create(mpa);
    }

    public Mpa update(Mpa mpa) {
        Mpa savedMpa = mpaStorage.findById(mpa.getId());
        if (mpa.getName() != null) savedMpa.setName(mpa.getName());
        mpaStorage.update(mpa);
        return savedMpa;
    }

    public void deleteAllMpa() {
        mpaStorage.deleteAll();
    }

    // вспомогательный метод для генерации нового идентификатора
    private Integer getNextId() {
        Integer currentMaxId = mpaStorage.findAll()
                .stream()
                .mapToInt(Mpa::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }
}

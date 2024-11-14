package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {
    private final BaseStorage<Mpa> mpaStorage;

    public List<Mpa> findAll() {
        List<Mpa> mpa = mpaStorage.findAll();
        log.info("Обработан запрос на получение всех рейтингов MPA");
        return mpa;
    }

    public Mpa findById(Long id) {
        Mpa mpa = mpaStorage.findById(id);
        log.info("Обработан запрос на получение рейтинга MPA с ID {}", id);
        return mpa;
    }

    public Mpa create(Mpa mpa) {
        Mpa createdMpa = mpaStorage.create(mpa);
        log.info("Создан MPA с ID {}", mpa.getId());
        return createdMpa;
    }

    public Mpa update(Mpa mpa) {
        Mpa savedMpa = mpaStorage.findById(mpa.getId());
        if (mpa.getName() != null) savedMpa.setName(mpa.getName());
        mpaStorage.update(savedMpa);
        log.info("Обновлен рейтинг MPA с ID {}", mpa.getId());
        return savedMpa;
    }

    public void deleteAllMpa() {
        mpaStorage.deleteAll();
        log.info("Удалены все рейтинги MPA");
    }
}

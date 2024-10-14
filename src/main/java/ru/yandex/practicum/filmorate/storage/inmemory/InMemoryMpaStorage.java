package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("inMemoryMpaStorage")
public class InMemoryMpaStorage implements MpaStorage {
    private final Map<Integer, Mpa> mpas = new HashMap<>();

    public InMemoryMpaStorage() {
        mpas.put(1, new Mpa(1, "G"));
        mpas.put(2, new Mpa(2, "PG"));
        mpas.put(3, new Mpa(3, "PG-13"));
        mpas.put(4, new Mpa(4, "R"));
        mpas.put(5, new Mpa(5, "NC-17"));
    }

    @Override
    public List<Mpa> findAll() {
        log.trace("Получен запрос на получение всех рейтингов MPA");
        return mpas.values().stream().toList();
    }

    @Override
    public void create(Mpa mpa) {
        mpas.put(mpa.getId(), mpa);
        log.trace("Добавлен новый рейтинг MPA с ID: {}", mpa.getId());
    }

    @Override
    public Mpa findById(Integer id) {
        log.trace("Получен запрос на получение рейтинга MPA с ID: {}", id);
        Mpa mpa = mpas.get(id);
        if (mpa == null) {
            log.warn("Рейтинг MPA с ID: {} не найден", id);
            throw new NotFoundException("Ретинг MPA с ID: " + id + " не найден");
            //throw new ValidationException("Ретинг MPA с ID: " + id + " не найден");
        }
        return mpa;
    }

    @Override
    public void update(Mpa mpa) {
        mpas.replace(mpa.getId(), mpa);
        log.trace("Обновлен рейтинг MPA с ID: {}", mpa.getId());
    }

    @Override
    public void deleteAll() {
        mpas.clear();
        log.trace("Удалены все рейтинги MPA");
    }
}

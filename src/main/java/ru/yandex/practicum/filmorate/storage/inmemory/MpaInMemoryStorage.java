package ru.yandex.practicum.filmorate.storage.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.storage", name = "in-memory", havingValue = "true")
public class MpaInMemoryStorage implements BaseStorage<Mpa> {
    private final Map<Long, Mpa> mpas = new HashMap<>();

    public MpaInMemoryStorage() {
        mpas.put(1L, new Mpa(1L, "G"));
        mpas.put(2L, new Mpa(2L, "PG"));
        mpas.put(3L, new Mpa(3L, "PG-13"));
        mpas.put(4L, new Mpa(4L, "R"));
        mpas.put(5L, new Mpa(5L, "NC-17"));
    }

    @Override
    public List<Mpa> findAll() {
        log.trace("Получен запрос на получение всех рейтингов MPA");
        return mpas.values().stream().toList();
    }

    @Override
    public Mpa create(Mpa mpa) {
        mpa.setId(getNextId());
        mpas.put(mpa.getId(), mpa);
        log.trace("Добавлен новый рейтинг MPA с ID: {}", mpa.getId());
        return mpa;
    }

    @Override
    public Mpa findById(Long id) {
        log.trace("Получен запрос на получение рейтинга MPA с ID: {}", id);
        Mpa mpa = mpas.get(id);
        if (mpa == null) {
            log.warn("Рейтинг MPA с ID: {} не найден", id);
            throw new NotFoundException("Ретинг MPA с ID: " + id + " не найден");
        }
        return mpa;
    }

    @Override
    public Mpa update(Mpa mpa) {
        mpas.replace(mpa.getId(), mpa);
        log.trace("Обновлен рейтинг MPA с ID: {}", mpa.getId());
        return mpa;
    }

    @Override
    public void deleteAll() {
        mpas.clear();
        log.trace("Удалены все рейтинги MPA");
    }

    // вспомогательный метод для генерации нового идентификатора
    private Long getNextId() {
        Long currentMaxId = mpas.values()
                .stream()
                .mapToLong(Mpa::getId)
                .max()
                .orElse(0);
        currentMaxId++;
        log.debug("Сгенерирован новый ID: {}", currentMaxId);
        return currentMaxId;
    }
}
